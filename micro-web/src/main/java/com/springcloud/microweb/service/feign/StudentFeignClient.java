package com.springcloud.microweb.service.feign;

import com.springcloud.microweb.entity.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/*
* 这里要注意fallback = StudentServiceFallback.class这种方式不能获取具体的异常
*/
@FeignClient(name = "MICRO-ORDER",path = "/feign",
//        fallback = StudentServiceFallback.class,
        fallbackFactory = StudentFeignFallbackFactory.class
        )
public interface StudentFeignClient {

    @PostMapping("/student/saveStudent")
    String saveStudent(@RequestBody Student student);

    @GetMapping("/student/getAllStudent")
    String getAllStudent();

    @GetMapping("/student/getStudentById")
    String getStudentById(@RequestParam("id") Integer id);

    @GetMapping("/student/errorMessage")
    String errorMessage(@RequestParam("id") Integer id);

    @GetMapping("/student/queryStudentTimeout")
    String queryStudentTimeout(@RequestParam("millis") int millis);
}
