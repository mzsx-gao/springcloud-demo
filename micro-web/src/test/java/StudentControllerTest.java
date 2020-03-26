import com.springcloud.microweb.MicroWebApplication;
import com.springcloud.microweb.controller.StudentController;
import com.springcloud.microweb.controller.UserController;
import com.springcloud.microweb.entity.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroWebApplication.class)
public class StudentControllerTest {

    @Autowired
    private StudentController stuController;

    @Test
    public void test() {
        Student stu = new Student();
        stu.setId(1);
        stu.setCard_num("12345");
        stu.setName("小高");
        System.out.println(stuController.saveStudent(stu));

        System.out.println(stuController.getAllStudent());

        System.out.println(stuController.getStudentById(1));
    }

    @Test
    public void test1(){
        System.out.println(stuController.errorMessage(1));
    }
}
