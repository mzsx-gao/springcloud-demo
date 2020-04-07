package com.gao.springcloud.custom.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Scope("customRefresh")
@RestController
public class CustomRefreshController {

    @Value("${xx.name:gsd}")
    private String name;

    @Autowired
    private Environment environment;

    @RequestMapping("/customRefresh/queryName")
    public String queryName() {
        System.out.println(this.hashCode()+"");
        System.out.println("@Value name = " + name);
        System.out.println("environment name = " + environment.getProperty("xx.name"));

        System.out.println("environment password = " + environment.getProperty("xx.password"));
        return name + "--->" + environment.getProperty("xx.name");
    }
}

