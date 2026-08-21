package com.foxminded.schoolmanagementapp.util.datagenerator;

import com.foxminded.schoolmanagementapp.dto.StudentDto;
import java.util.List;

public interface StudentGenerator {

    List<StudentDto> generateStudentsWithGroups(List<Long> groupIds);
}
