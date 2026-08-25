package com.foxminded.schoolmanagementapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.GlobalMapper;
import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import com.foxminded.schoolmanagementapp.repository.StudentsRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock private StudentsRepository studentsRepository;
    @Mock private CourseRepository courseRepository;
    @InjectMocks private StudentService service;
    @Spy GlobalMapper mapper;

    @Test
    void findStudentsByCourseName_shouldReturnStudentsEnrolledInCourse() {
        String courseName = "Java";
        List<Student> expected =
                List.of(new Student(1L, 5L, "John", "Smith"), new Student(2L, 5L, "Anna", "Brown"));
        when(studentsRepository.findByCourseName(courseName)).thenReturn(expected);

        List<StudentDto> actual = service.findStudentsByCourseName("Java");

        assertThat(actual)
                .extracting("id", "groupId", "firstName", "lastName")
                .containsExactlyInAnyOrder(
                        tuple(1L, 5L, "John", "Smith"), tuple(2L, 5L, "Anna", "Brown"));

        verify(studentsRepository).findByCourseName(courseName);
        verify(mapper).toStudentDto(new Student(1L, 5L, "John", "Smith"));
        verify(mapper).toStudentDto(new Student(2L, 5L, "Anna", "Brown"));
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
        Student studentToSave = new Student(null, 5L, "John", "Smith");

        Student savedStudent = new Student(1L, 5L, "John", "Smith");
        when(studentsRepository.save(studentToSave)).thenReturn(savedStudent);

        StudentDto actual = service.addStudent(dto);

        assertThat(actual).isEqualTo(new StudentDto(1L, 5L, "John", "Smith"));
        verify(studentsRepository).save(studentToSave);

        verify(mapper).toStudent(new StudentDto(null, 5L, "John", "Smith"));
        verify(mapper).toStudentDto(new Student(1L, 5L, "John", "Smith"));
    }

    @Test
    void addStudents_shouldSaveAndReturnStudentBatch() {
        List<StudentDto> students =
                List.of(
                        new StudentDto(null, 5L, "John", "Smith"),
                        new StudentDto(null, null, "Anna", "Brown"));
        List<Student> studentsToSave =
                List.of(
                        new Student(null, 5L, "John", "Smith"),
                        new Student(null, null, "Anna", "Brown"));
        List<Student> savedStudents =
                List.of(
                        new Student(1L, 5L, "John", "Smith"),
                        new Student(2L, null, "Anna", "Brown"));
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
}
