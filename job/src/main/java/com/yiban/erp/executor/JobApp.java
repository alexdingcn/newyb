package com.yiban.erp.executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
//@ComponentScan(basePackages = "com.yiban.erp.executor")
public class JobApp {

    public static void main(String[] args) {
        SpringApplication.run(JobApp.class);
    }
}
