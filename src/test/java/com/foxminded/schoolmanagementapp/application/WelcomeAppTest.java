package com.foxminded.schoolmanagementapp.application;

import com.foxminded.schoolmanagementapp.util.datagenerator.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

@ExtendWith(MockitoExtension.class)
class WelcomeAppTest {

    @Mock
    DataGenerator dataGenerator;
    @Mock
    private ApplicationArguments applicationArguments;

    @Test
    void run_shouldPrintExpectedMessage() {

    }
}