package com.foxminded.schoolmanagementapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SchoolManagementAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolManagementAppApplication.class, args);
    }
}
