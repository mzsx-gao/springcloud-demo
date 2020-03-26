package com.springcloud.microorder.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class StudentController {

    List<Map<Integer, Student>> students = new ArrayList<>();

    @RequestMapping("/feign/student/saveStudent")
    public String saveStudent(@RequestBody Student student) {
        log.info("添加学生.." + student.toString());
        Map<Integer, Student> stuMap = new HashMap<>();
        stuMap.put(student.getId(), student);
        students.add(stuMap);
        return "success";
    }

    @RequestMapping("/feign/student/getAllStudent")
    public String getAllStudent() {
        log.info("查询所有学生信息");
        return JSONObject.toJSONString(students);
    }

    @RequestMapping("/feign/student/getStudentById")
    public String queryStudentById(@RequestParam("id") Integer id) {
        log.info("查询学生信息:id=" + id);
        if (students.size() > 0) {
            for (Map<Integer, Student> stuMap : students) {
                if (stuMap.get(id) != null) {
                    Student stu = stuMap.get(id);
                    return JSONObject.toJSONString(stu);
                }
            }
        }
        return "";
    }

    @RequestMapping("/feign/student/errorMessage")
    public String errorMessage(@RequestParam("id") Integer id) {
        log.info("这里抛出异常...");
        int a = 1 / 0;
        return "";
    }

    @RequestMapping("/feign/student/queryStudentTimeout")
    public String queryStudentTimeout(@RequestParam("millis") int millis) {
        log.info("provider--->" + millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "provider--->" + millis;
    }
}
