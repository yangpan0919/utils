package com.example.serialproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class SerialProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SerialProxyApplication.class, args);
    }
}
