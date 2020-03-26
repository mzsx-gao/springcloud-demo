package com.springcloud.microweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"com.springcloud.microweb"})
@EnableEurekaClient
//开启断路器功能
@EnableCircuitBreaker
//开启feign支持
@EnableFeignClients(basePackages = { "com.springcloud.microweb.service.feign" })
public class MicroWebApplication {

    @Bean
    //负载均衡注解
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MicroWebApplication.class,args);
    }
}
