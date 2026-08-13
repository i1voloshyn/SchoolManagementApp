package com.foxminded.schoolmanagementapp.application;

import com.foxminded.schoolmanagementapp.service.InitialDataGenerator;
import com.foxminded.schoolmanagementapp.util.datagenerator.DataGenerator;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Order(1)
public class DataInitialisationStartupRunner implements ApplicationRunner {
    private final DataGenerator dataGenerator;

    @Override
    public void run(ApplicationArguments args) {
        dataGenerator.generateDataIfEmpty();
    }
}
