package com.springcloud.microweb.service.feign;

import com.springcloud.microweb.entity.Student;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StudentFeignFallbackFactory implements FallbackFactory<StudentFeignClient> {

    /**
     * 如果feign定义有过滤器(如:FeignErrMessageFilter)，则先走过滤器,然后才会调用这个create()方法
     */
    @Override
    public StudentFeignClient create(Throwable throwable) {
        if(throwable == null) {
            return null;
        }
        final String msg = throwable.getMessage();
        log.info("exception:" + msg);
        return new StudentFeignClient() {
            @Override
            public String getAllStudent() {
                log.info("exception=====getAllStudent==========" + msg);
                return msg;
            }

            @Override
            public String saveStudent(Student student) {
                log.info("exception=====saveStudent==========" + msg);
                return msg;
            }

            @Override
            public String getStudentById(Integer id) {
                log.info("exception=====getStudentById==========" + msg);
                return msg;
            }

            @Override
            public String errorMessage(Integer id) {
                log.info("exception=====errorMessage==========" + msg);
                return msg;
            }

            @Override
            public String queryStudentTimeout(int millis) {
                log.info("exception=====queryStudentTimeout==========" + msg);
                return msg;
            }
        };
    }
}
