package com.yiban.erpcustomer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = "com.yiban.erpcustomer")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
