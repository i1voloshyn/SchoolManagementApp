package com.foxminded.schoolmanagementapp.util;

import com.foxminded.schoolmanagementapp.config.DataGeneratorProperties;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.util.datagenerator.CoursesGenerator;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@AllArgsConstructor
@Component
public class FakerCoursesDataGenerator implements CoursesGenerator {
    private static final int COURSE_DESCRIPTION_LENGTH = 8;
    private static final String COURSE_NAME_TEMPLATE = "Course-%03d: %s";

    private final Faker faker;
    private final DataGeneratorProperties properties;

    @Override
    public List<CourseDto> generateCourses() {
        return IntStream.range(0, properties.coursesCount())
                .mapToObj(index -> new CourseDto(
                        null,
                        COURSE_NAME_TEMPLATE.formatted(
                                index,
                                faker.programmingLanguage().name()
                        ),
                        faker.lorem().sentence(COURSE_DESCRIPTION_LENGTH)
                ))
                .toList();
    }
}
