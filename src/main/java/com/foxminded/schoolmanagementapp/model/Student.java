package com.foxminded.schoolmanagementapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private @Nullable Long id;
    private @Nullable Long groupId;
    private String firstName;
    private String lastName;
}
