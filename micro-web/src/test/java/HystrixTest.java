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
