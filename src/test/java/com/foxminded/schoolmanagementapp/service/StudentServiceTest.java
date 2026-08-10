package com.foxminded.schoolmanagementapp.service;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.repository.CourseRepository;
import com.foxminded.schoolmanagementapp.repository.StudentsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentsRepository studentsRepository;
    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private StudentService service;

    @Test
    void findStudentsByCourseName_shouldReturnStudentsEnrolledInCourse() {
        Long courseId = 10L;
        Course course = new Course(courseId, "Java", "Java course");
        List<Student> expected = List.of(
                new Student(1L, 5L, "John", "Smith"),
                new Student(2L, 5L, "Anna", "Brown")
        );
        when(courseRepository.findByName("Java")).thenReturn(Optional.of(course));
        when(studentsRepository.findByCourseId(courseId)).thenReturn(expected);

        List<Student> actual = service.findStudentsByCourseName("Java");

        assertThat(actual).containsExactlyElementsOf(expected);
        verify(studentsRepository).findByCourseId(courseId);
    }

    @Test
    void findStudentsByCourseName_shouldThrowException_whenCourseDoesNotExist() {
        when(courseRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThatExceptionOfType(CourseNotFoundException.class)
                .isThrownBy(() -> service.findStudentsByCourseName("Unknown"));
        verifyNoInteractions(studentsRepository);
    }

    @Test
    void findStudentsByCourseName_shouldRejectBlankCourseName() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.findStudentsByCourseName(" "));
        verifyNoInteractions(courseRepository, studentsRepository);
    }

    @Test
    void addStudent_shouldReturnSavedStudent() {
        Student newStudent = new Student(null, 5L, "John", "Smith");
        Student savedStudent = new Student(1L, 5L, "John", "Smith");
        when(studentsRepository.save(newStudent)).thenReturn(savedStudent);

        Student actual = service.addStudent(newStudent);

        assertThat(actual).isEqualTo(savedStudent);
        verify(studentsRepository).save(newStudent);
    }

    @Test
    void addStudent_shouldRejectStudentWithId() {
        Student existingStudent = new Student(1L, 5L, "John", "Smith");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.addStudent(existingStudent));
        verifyNoInteractions(studentsRepository);
    }

    @Test
    void addStudent_shouldRejectBlankFirstName() {
        Student student = new Student(null, 5L, " ", "Smith");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.addStudent(student));
        verifyNoInteractions(studentsRepository);
    }

    @Test
    void addStudent_shouldRejectBlankLastName() {
        Student student = new Student(null, 5L, "John", null);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.addStudent(student));
        verifyNoInteractions(studentsRepository);
    }

    @Test
    void deleteStudent_shouldDeleteExistingStudent() {
        Long studentId = 1L;
        Student student = new Student(studentId, 5L, "John", "Smith");
        when(studentsRepository.findById(studentId)).thenReturn(Optional.of(student));

        service.deleteStudent(studentId);

        verify(studentsRepository).delete(studentId);
    }

    @Test
    void deleteStudent_shouldThrowException_whenStudentDoesNotExist() {
        Long studentId = 99L;
        when(studentsRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(StudentNotFoundException.class)
                .isThrownBy(() -> service.deleteStudent(studentId));
        verify(studentsRepository, never()).delete(studentId);
    }

    @Test
    void deleteStudent_shouldRejectNonPositiveId() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.deleteStudent(-1L));
        verifyNoInteractions(studentsRepository);
    }
}
