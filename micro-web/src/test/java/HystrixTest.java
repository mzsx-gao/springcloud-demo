import com.springcloud.microweb.MicroWebApplication;
import com.springcloud.microweb.service.UserService;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MicroWebApplication.class)
public class HystrixTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Integer count = 11;

    private CountDownLatch cdl = new CountDownLatch(count);

    @Autowired
    UserService userService;

    @Rule
    public ContiPerfRule contiPerfRule = new ContiPerfRule();

    /**
     * 以下两个方法是为了测试hystrix的隔离策略
     * 结论：
     * 1.线程池隔离策略，hystrix 是会单独创建线程的
     * 2.信号量隔离不会单独开启线程，是采用一个全局变量来控制并发量，一个请求过来全局变量加 1，
     *   单加到跟配置 中的大小相等是就不再接受用户请求了
     */
    @Test
    public void hystrixTest() throws Exception{
        for (Integer i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    cdl.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("线程名："+Thread.currentThread().getName() + "结果==>" + userService.queryContents());
            }).start();
            cdl.countDown();
        }
        Thread.currentThread().join();
    }

    @Test
    @PerfTest(invocations = 11,threads = 11)
    public void hystrixTest2() throws Exception{
        logger.info(Thread.currentThread().getName() + "==>" + userService.queryContents2());
        Thread.currentThread().join();
    }
}
