package com.foxminded.schoolmanagementapp.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.foxminded.schoolmanagementapp.config.DataGeneratorProperties;
import com.foxminded.schoolmanagementapp.dto.CourseDto;
import com.foxminded.schoolmanagementapp.util.datagenerator.CoursesGenerator;
import java.util.List;
import java.util.Random;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

class FakerCoursesDataGeneratorTest {
    private final Faker faker = new Faker(new Random(1));
    private final DataGeneratorProperties properties = new DataGeneratorProperties(200, 10, 10);

    private final CoursesGenerator generator = new FakerCoursesDataGenerator(faker, properties);

    @Test
    void generateCourses_shouldGenerateConfiguredNumberOfValidUniqueCourses() {
        List<CourseDto> actual = generator.generateCourses();

        assertThat(actual)
                .hasSize(properties.coursesCount())
                .allSatisfy(
                        course -> {
                            assertThat(course.id()).isNull();
                            assertThat(course.name()).isNotBlank();
                        });
        assertThat(actual).extracting(CourseDto::name).doesNotHaveDuplicates();
    }
}
