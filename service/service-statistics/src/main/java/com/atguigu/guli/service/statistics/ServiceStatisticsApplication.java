package com.atguigu.guli.service.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@SpringBootApplication
@EnableFeignClients
@ComponentScan({"com.atguigu.guli"})
@EnableCircuitBreaker
public class ServiceStatisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceStatisticsApplication.class,args);
    }
}
