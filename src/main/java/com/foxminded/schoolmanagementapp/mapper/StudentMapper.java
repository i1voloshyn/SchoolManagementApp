package com.foxminded.schoolmanagementapp.mapper;

import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.model.Student;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StudentMapper {

    @Mapping(target = "groupId", source = "group.id")
    StudentDto toStudentDto(Student student);


    @Mapping(target = "group", source = "groupId")
    @Mapping(target = "courses", ignore = true)
    Student toStudent(StudentDto studentDto);

    default Group toGroup(Long groupId) {
        if (groupId == null) {
            return null;
        }

        Group group = new Group();
        group.setId(groupId);
        return group;
    }
}
