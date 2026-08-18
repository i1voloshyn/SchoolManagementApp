package com.foxminded.schoolmanagementapp.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Faker fakerImpl() {
        return new Faker();
    }

}
