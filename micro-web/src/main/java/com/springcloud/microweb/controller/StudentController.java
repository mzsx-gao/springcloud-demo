package com.springcloud.microweb.controller;

import com.springcloud.microweb.entity.Student;
import com.springcloud.microweb.service.feign.StudentFeignClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentFeignClient studentFeignClient;

    @RequestMapping("/saveStudent")
    public String saveStudent(@RequestBody Student student) {
        return studentFeignClient.saveStudent(student);
    }

    @RequestMapping("/getAllStudent")
    public String getAllStudent() {
        return studentFeignClient.getAllStudent();
    }

    @RequestMapping("/getStudentById")
    public String getStudentById(Integer id) {
        return studentFeignClient.getStudentById(id);
    }

    @RequestMapping("/errorMessage")
    public String errorMessage(Integer id) {
        try {
            return studentFeignClient.errorMessage(id);
        }catch (FeignException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping("/timeOut")
    public String timeOutTest(@RequestParam int millis) {

        long t1 = System.currentTimeMillis();
        String cacheResult = studentFeignClient.queryStudentTimeout(millis);
        long t2 = System.currentTimeMillis();

        log.info("======调用provider耗时：" + (t2 - t1) + "ms");
        return cacheResult;
    }
}
