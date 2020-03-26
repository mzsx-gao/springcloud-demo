import com.springcloud.microweb.MicroWebApplication;
import com.springcloud.microweb.controller.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroWebApplication.class)
public class UserControllerTest {

    @Autowired
    private UserController userController;

    /**
     * 测试ribbon负载均衡，请求会随机的发送到8084和8085两台机器
     */
    @Test
    public void testContexLoads() {
        for (int i = 0; i < 100; i++) {
            System.out.println(userController.queryUser());
        }
    }
}
