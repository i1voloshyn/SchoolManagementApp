package com.foxminded.schoolmanagementapp.mapper;

import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.model.Course;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CourseMapper {

    CourseDto toCourseDto(Course course);

    @Mapping(target = "students", ignore = true)
    Course toCourse(CourseDto courseDto);
}
