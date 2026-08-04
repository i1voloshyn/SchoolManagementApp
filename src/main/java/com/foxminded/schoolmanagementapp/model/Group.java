package com.foxminded.schoolmanagementapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Nullable
    Integer groupId;
    String name;
}
