package com.cloudsec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CloudSecurityApp {
    public static void main(String[] args) {
        SpringApplication.run(CloudSecurityApp.class, args);
    }
}