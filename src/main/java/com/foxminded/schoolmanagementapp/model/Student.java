package com.foxminded.schoolmanagementapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Nullable Long studentId;
    @Nullable Integer groupId;
    String firstName;
    String lastName;
}
