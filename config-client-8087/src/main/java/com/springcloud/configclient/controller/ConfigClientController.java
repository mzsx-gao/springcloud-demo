package com.springcloud.configclient.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configclient")
@Slf4j
@RefreshScope
public class ConfigClientController {

    @Value("${demo.username}")
    private String username;

    @Autowired
    Environment environment;

    @RequestMapping("/test")
    public String queryContent() {
        log.info("@Value======username======" + username);
        log.info("Environment======username======" + environment.getProperty("demo.username"));
        return "@Value======username======" + username+"\n"+
                "Environment======username======" + environment.getProperty("demo.username");
    }
}
