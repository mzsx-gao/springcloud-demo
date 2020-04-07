package com.gao.springcloud.custom.refresh;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class CustomRefreshHandler implements ApplicationContextAware {

    private static String connnectStr = "127.0.0.1:2181";

    private static CuratorFramework client;

    private static String path = "/config";

    @Value(("${zookeeper.config.enable:false}"))
    private boolean enbale;

    @Autowired
    Environment environment;

    private static String zkPropertyName = "zookeeperSource";

    private static String scopeName = "customRefresh";

    private static ConfigurableApplicationContext applicationContext;

    private ConcurrentHashMap map = new ConcurrentHashMap();

    private BeanDefinitionRegistry beanDefinitionRegistry;

    //初始化动作
    @PostConstruct
    public void init() {
        if (!enbale) return;

        CustomRefreshScopeRegistry refreshScopeRegistry = (CustomRefreshScopeRegistry)applicationContext.getBean("customRefreshScopeRegistry");
        beanDefinitionRegistry = refreshScopeRegistry.getBeanDefinitionRegistry();

        client = CuratorFrameworkFactory.
                builder().
                connectString(connnectStr).
                sessionTimeoutMs(5000).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).
                build();

        client.start();
        try {
            Stat stat = client.checkExists().forPath(path);
            if (stat == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                        forPath(path, "zookeeper config".getBytes());
                TimeUnit.SECONDS.sleep(1);
            } else {
                //1、把config下面的子节点加载到spring容器的属性对象中
                addChildToSpringProperty(client, path);
            }
//            nodeCache(client,path);
            //监听子节点的变化（即监听配置的变化，实现动态刷新）
            childNodeCache(client, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //把config下面的子节点加载到spring容器的属性对象中
    private void addChildToSpringProperty(CuratorFramework client, String path) {
        if (!checkExistsSpringProperty()) {
            //如果不存在zookeeper的配置属性对象则创建
            createZookeeperSpringProperty();
        }

        //把config目录下的子节点添加到 zk的PropertySource对象中
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        PropertySource<?> propertySource = propertySources.get(zkPropertyName);
        ConcurrentHashMap zkmap = (ConcurrentHashMap) propertySource.getSource();
        try {
            List<String> strings = client.getChildren().forPath(path);
            for (String string : strings) {
                zkmap.put(string, new String(client.getData().forPath(path + "/" + string)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkExistsSpringProperty() {
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (zkPropertyName.equals(propertySource.getName())) {
                return true;
            }
        }
        return false;
    }

    private void createZookeeperSpringProperty() {
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        OriginTrackedMapPropertySource zookeeperSource = new OriginTrackedMapPropertySource(zkPropertyName, map);
        propertySources.addLast(zookeeperSource);
    }

    /**
     * 子节点变化监听器
     * @param client
     * @param path
     */
    private void childNodeCache(CuratorFramework client, String path) {
        try {
            final PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, false);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            System.out.println("增加了节点");
                            addOrUpdateEnv(event.getData(), client);
                            break;
                        case CHILD_REMOVED:
                            System.out.println("删除了节点");
                            delEnv(event.getData());
                            break;
                        case CHILD_UPDATED:
                            System.out.println("更新了节点");
                            addOrUpdateEnv(event.getData(), client);
                            break;
                        default:
                            break;
                    }
                    //对customRefresh作用域的实例进行刷新
                    refreshBean();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 该方法内部会重新给spring环境里的配置属性赋值
     * 子节点新增和修改内容都会调用到这个方法
     */
    private void addOrUpdateEnv(ChildData childData, CuratorFramework client) {
        ChildData next = childData;
        String childpath = next.getPath();
        String data = null;
        try {
            data = new String(client.getData().forPath(childpath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (zkPropertyName.equals(propertySource.getName())) {
                OriginTrackedMapPropertySource ps = (OriginTrackedMapPropertySource) propertySource;
                ConcurrentHashMap chm = (ConcurrentHashMap) ps.getSource();
                chm.put(childpath.substring(path.length() + 1), data);
            }
        }
    }

    /**
     * 删除spring环境里的配置属性
     */
    private void delEnv(ChildData childData) {
        ChildData next = childData;
        String childpath = next.getPath();
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (zkPropertyName.equals(propertySource.getName())) {
                OriginTrackedMapPropertySource ps = (OriginTrackedMapPropertySource) propertySource;
                ConcurrentHashMap chm = (ConcurrentHashMap) ps.getSource();
                chm.remove(childpath.substring(path.length() + 1));
            }
        }
    }

    /**
     * 对customRefresh作用域的实例进行刷新
     */
    private void refreshBean() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);
            if(scopeName.equals(beanDefinition.getScope())) {
                //销毁bean
                applicationContext.getBeanFactory().destroyScopedBean(beanDefinitionName);
                //重新实例化bean
                applicationContext.getBean(beanDefinitionName);
            }
        }
    }

    private void nodeCache(final CuratorFramework client, final String path) {

        try {
            //第三个参数是是否压缩
            //就是对path节点进行监控，是一个事件模板
            final NodeCache nodeCache = new NodeCache(client, path, false);
            nodeCache.start();

            //这个就是事件注册
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    byte[] data = nodeCache.getCurrentData().getData();
                    String path1 = nodeCache.getCurrentData().getPath();

                    Object put = map.put(path1.replace("/", ""), new String(data));
                    MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
                    OriginTrackedMapPropertySource zookeeperSource = new OriginTrackedMapPropertySource("zookeeper source", map);
                    propertySources.addLast(zookeeperSource);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        CustomRefreshHandler.applicationContext = (ConfigurableApplicationContext) context;
    }
}
