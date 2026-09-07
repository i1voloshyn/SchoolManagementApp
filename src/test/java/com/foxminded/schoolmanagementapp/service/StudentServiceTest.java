package com.foxminded.schoolmanagementapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.GlobalMapper;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import com.foxminded.schoolmanagementapp.repository.StudentsRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentsRepository studentsRepository;
    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private StudentService service;
    @Spy
    GlobalMapper mapper;

    @Test
    void findStudentsByCourseName_shouldReturnStudentsEnrolledInCourse() {
        String courseName = "Java";
        Course course = Course.builder().id(5L).name(courseName).description("Java programming course").build();

        List<Student> expected =
                List.of(student(1L, "John", "Smith", course),
                        student(2L, "Anna", "Brown", course));
        when(studentsRepository.findByCourseName(courseName)).thenReturn(expected);

        List<StudentDto> actual = service.findStudentsByCourseName("Java");

        assertThat(actual)
                .extracting("id", "groupId", "firstName", "lastName")
                .containsExactlyInAnyOrder(
                        tuple(1L, 5L, "John", "Smith"), tuple(2L, 5L, "Anna", "Brown"));

        verify(studentsRepository).findByCourseName(courseName);
        verify(mapper).toStudentDto(student(1L, "John", "Smith", course));
        verify(mapper).toStudentDto(student(2L, "Anna", "Brown", course));
    }

    @Test
    void findStudentsByCourseName_shouldReturnEmptyList_whenCourseDoesNotExist() {
        String nonExistedCourse = "NonExistedCourse";
        when(studentsRepository.findByCourseName(nonExistedCourse)).thenReturn(List.of());

        var actual = service.findStudentsByCourseName(nonExistedCourse);

        assertThat(actual).isEmpty();
    }

    @Test
    void findStudentsByCourseName_shouldRejectBlankCourseName() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.findStudentsByCourseName(" "));
        verifyNoInteractions(courseRepository, studentsRepository, mapper);
    }

    @Test
    void addStudent_shouldReturnSavedStudent() {
        StudentDto dto = new StudentDto(null, 5L, "John", "Smith");
        Student studentToSave = student(null, "John", "Smith", course(5L, "Java", "Java programming course"));

        Student savedStudent = student(1L, "John", "Smith", course(5L, "Java", "Java programming course"));
        when(studentsRepository.save(studentToSave)).thenReturn(savedStudent);

        StudentDto actual = service.addStudent(dto);

        assertThat(actual).isEqualTo(new StudentDto(1L, 5L, "John", "Smith"));
        verify(studentsRepository).save(studentToSave);

        verify(mapper).toStudent(new StudentDto(null, 5L, "John", "Smith"));
        verify(mapper).toStudentDto(student(1L, "John", "Smith",
                course(5L, "Java", "Java programming course")));
    }

    private Course course(long l, String java, String javaProgrammingCourse) {
        return Course.builder().id(l).name(java).description(javaProgrammingCourse).build();
    }

    @Test
    void addStudents_shouldSaveAndReturnStudentBatch() {
        List<StudentDto> students =
                List.of(
                        new StudentDto(null, 5L, "John", "Smith"),
                        new StudentDto(null, null, "Anna", "Brown"));
        List<Student> studentsToSave =
                List.of(
                        student(null, "John", "Smith", course(5L, "Java", "Java programming course")),
                        student(null, "Anna", "Brown", null));
        List<Student> savedStudents =
                List.of(
                        student(1L, "John", "Smith", course(5L, "Java", "Java programming course")),
                        student(2L, "Anna", "Brown", null));
        when(studentsRepository.saveAll(studentsToSave)).thenReturn(savedStudents);

        List<StudentDto> actual = service.addStudents(students);

        assertThat(actual)
                .containsExactly(
                        new StudentDto(1L, 5L, "John", "Smith"),
                        new StudentDto(2L, null, "Anna", "Brown"));
        verify(studentsRepository).saveAll(studentsToSave);
    }

    @Test
    void addStudents_shouldRejectBatchContainingInvalidStudent() {
        List<StudentDto> students =
                List.of(
                        new StudentDto(null, 5L, "John", "Smith"),
                        new StudentDto(2L, null, "Anna", "Brown"));

        assertThatIllegalArgumentException().isThrownBy(() -> service.addStudents(students));
        verifyNoInteractions(studentsRepository, mapper);
    }

    @Test
    void addStudent_shouldRejectStudentWithId() {
        StudentDto existingStudent = new StudentDto(1L, 5L, "John", "Smith");

        assertThatIllegalArgumentException().isThrownBy(() -> service.addStudent(existingStudent));
        verifyNoInteractions(studentsRepository, mapper);
    }

    @Test
    void addStudent_shouldRejectBlankFirstName() {
        StudentDto studentDto = new StudentDto(null, 5L, " ", "Smith");

        assertThatIllegalArgumentException().isThrownBy(() -> service.addStudent(studentDto));
        verifyNoInteractions(studentsRepository, mapper);
    }

    @Test
    void addStudent_shouldRejectBlankLastName() {
        StudentDto student = new StudentDto(null, 5L, "John", null);

        assertThatIllegalArgumentException().isThrownBy(() -> service.addStudent(student));
        verifyNoInteractions(studentsRepository, mapper);
    }

    @Test
    void deleteStudent_shouldDeleteExistingStudent() {
        Long studentId = 1L;

        service.deleteStudent(studentId);

        verify(studentsRepository).delete(studentId);
    }

    @Test
    void deleteStudent_shouldRejectNonPositiveId() {
        assertThatIllegalArgumentException().isThrownBy(() -> service.deleteStudent(-1L));
        verifyNoInteractions(studentsRepository);
    }

    private Student student(Long id, String firstName, String lastName, Course course) {
        return Student.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .courses(Set.of(course))
                .build();
    }
}
