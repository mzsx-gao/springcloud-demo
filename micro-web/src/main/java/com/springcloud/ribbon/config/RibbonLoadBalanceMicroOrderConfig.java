package com.springcloud.ribbon.config;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
* 这个类最好不要出现在启动类的@ComponentScan扫描范围
* 如果出现在@ComponentScan扫描访问，那么这个配置类就是每个服务共用的配置了
* */
@Configuration
public class RibbonLoadBalanceMicroOrderConfig {

//    @RibbonClientName
    private String name = "micro-order";

    @Bean
    @ConditionalOnClass
    public IClientConfig defaultClientConfigImpl() {
        DefaultClientConfigImpl config = new DefaultClientConfigImpl();
        config.loadProperties(name);
        // 对当前实例的重试次数 当Eureka中可以找到服务，但是服务连不上时将会重试
        config.set(CommonClientConfigKey.MaxAutoRetries,2);
        // 切换实例的重试次数
        config.set(CommonClientConfigKey.MaxAutoRetriesNextServer,2);
        // 连接超时时间
        config.set(CommonClientConfigKey.ConnectTimeout,2000);
        // 请求处理超时时间
        config.set(CommonClientConfigKey.ReadTimeout,4000);
        //无论是请求超时或者socket read timeout都进行重试,注意这里设置为true比较危险,如果服务端没做幂等性处理，后果严重
        config.set(CommonClientConfigKey.OkToRetryOnAllOperations,true);
        return config;
    }

    // 判断服务是否存活,不建议使用
//    @Bean
//    public IPing iPing() {
//        //这个实现类会去调用服务来判断服务是否存活
//        return new PingUrl();
//    }

    @Bean
    public IRule ribbonRule() {
        //线性轮训
        new RoundRobinRule();
        //可以重试的轮训
        new RetryRule();
        //根据运行情况来计算权重
        new WeightedResponseTimeRule();
        //过滤掉故障实例，选择请求数最小的实例
        new BestAvailableRule();
        return new RandomRule();
    }
}
