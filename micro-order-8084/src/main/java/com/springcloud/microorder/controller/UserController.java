package com.springcloud.microorder.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Slf4j
@RestController
public class UserController {

    @RequestMapping("/queryUser")
    public String queryUser() throws Exception{
//        int num = new Random().nextInt(3);
//        if(num == 1 || num == 0){
//            throw new Exception("异常了");
//        }
        log.info("========micro-order-8084===queryUser");
        return "========micro-order-8084===queryUser";
    }
}
