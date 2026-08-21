package com.foxminded.schoolmanagementapp.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Configuration
public class AppConfig {
    @Bean
    public Faker fakerImpl() {
        return new Faker();
    }

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

}
