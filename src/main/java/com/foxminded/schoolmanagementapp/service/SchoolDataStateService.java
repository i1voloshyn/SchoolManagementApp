package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.repository.SchoolDataStateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SchoolDataStateService {

    private final SchoolDataStateRepository repository;

    public boolean isDatabaseEmpty() {
        return !repository.hasData();
    }
}
