package com.foxminded.schoolmanagementapp.util.enrollment;

import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.model.Enrollment;

import java.util.List;

public interface EnrollmentsRule {
    List<Enrollment> apply(List<StudentDto> students, List<CourseDto> courses);
}
