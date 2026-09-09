package com.foxminded.schoolmanagementapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.foxminded.schoolmanagementapp.dto.StudentDto;
import com.foxminded.schoolmanagementapp.mapper.StudentMapper;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.model.Student;
import com.foxminded.schoolmanagementapp.repository.StudentsRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentsRepository studentsRepository;
    @InjectMocks
    private StudentService service;
    @Spy
    StudentMapper studentMapper = Mappers.getMapper(StudentMapper.class);

    @Test
    void findStudentsByCourseName_shouldReturnStudentsEnrolledInCourse() {
        String courseName = "Java";
        Course course = Course.builder().id(5L).name(courseName).description("Java programming course").build();
        Group group = Group.builder().id(5L).name("Group A").build();
        Student student1 = student(1L, "John", "Smith", group, course);
        Student student2 = student(2L, "Anna", "Brown", group, course);

        List<Student> expected =
                List.of(student1, student2);
        when(studentsRepository.findByCourseName(courseName)).thenReturn(expected);

        List<StudentDto> actual = service.findStudentsByCourseName(courseName);

        assertThat(actual)
                .extracting("id", "groupId", "firstName", "lastName")
                .containsExactlyInAnyOrder(
                        tuple(1L, 5L, "John", "Smith"), tuple(2L, 5L, "Anna", "Brown"));

        verify(studentsRepository).findByCourseName(courseName);
        verify(studentMapper).toStudentDto(student1);
        verify(studentMapper).toStudentDto(student2);
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
        verifyNoInteractions(studentsRepository, studentMapper);
    }

    @Test
    void addStudent_shouldReturnSavedStudent() {
        StudentDto dto = new StudentDto(null, 5L, "John", "Smith");
        Group group = Group.builder().id(5L).name("Group A").build();
        Course course = course(5L, "Java", "Java programming course");


        Student savedStudent = student(1L, "John", "Smith", group, course);
        when(studentsRepository.save(any(Student.class))).thenReturn(savedStudent);

        StudentDto actual = service.addStudent(dto);

        assertThat(actual).isEqualTo(new StudentDto(1L, 5L, "John", "Smith"));
        verify(studentsRepository).save(any(Student.class));

        verify(studentMapper).toStudent(new StudentDto(null, 5L, "John", "Smith"));
        verify(studentMapper).toStudentDto(savedStudent);
    }

    private Course course(long l, String java, String javaProgrammingCourse) {
        return Course.builder().id(l).name(java).description(javaProgrammingCourse).build();
    }

    @Test
    void addStudents_shouldSaveAndReturnStudentBatch() {
        Group group = Group.builder().id(5L).name("Group A").build();
        Course course = course(5L, "Java", "Java programming course");
        List<StudentDto> students =
                List.of(
                        new StudentDto(null, 5L, "John", "Smith"),
                        new StudentDto(null, 5L, "Anna", "Brown"));
        List<Student> savedStudents =
                List.of(
                        student(1L, "John", "Smith", group, course),
                        student(2L, "Anna", "Brown", group, course));
        when(studentsRepository.saveAll(any(List.class))).thenReturn(savedStudents);

        List<StudentDto> actual = service.addStudents(students);

        assertThat(actual)
                .containsExactly(
                        new StudentDto(1L, 5L, "John", "Smith"),
                        new StudentDto(2L, 5L, "Anna", "Brown"));
        verify(studentsRepository).saveAll(any(List.class));
        students.forEach(student -> verify(studentMapper).toStudent(student));
        savedStudents.forEach(student -> verify(studentMapper).toStudentDto(student));
    }

    @Test
    void addStudents_shouldRejectBatchContainingInvalidStudent() {
        List<StudentDto> students =
                List.of(
                        new StudentDto(null, 5L, "John", "Smith"),
                        new StudentDto(2L, null, "Anna", "Brown"));

        assertThatIllegalArgumentException().isThrownBy(() -> service.addStudents(students));
        verifyNoInteractions(studentsRepository, studentMapper);
    }

    @Test
    void addStudent_shouldRejectStudentWithId() {
        StudentDto existingStudent = new StudentDto(1L, 5L, "John", "Smith");

        assertThatIllegalArgumentException().isThrownBy(() -> service.addStudent(existingStudent));
        verifyNoInteractions(studentsRepository, studentMapper);
    }

    @Test
    void addStudent_shouldRejectBlankFirstName() {
        StudentDto studentDto = new StudentDto(null, 5L, " ", "Smith");

        assertThatIllegalArgumentException().isThrownBy(() -> service.addStudent(studentDto));
        verifyNoInteractions(studentsRepository, studentMapper);
    }

    @Test
    void addStudent_shouldRejectBlankLastName() {
        StudentDto student = new StudentDto(null, 5L, "John", null);

        assertThatIllegalArgumentException().isThrownBy(() -> service.addStudent(student));
        verifyNoInteractions(studentsRepository, studentMapper);
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

    private Student student(Long id, String firstName, String lastName, Group group, Course course) {
        return Student.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .group(group)
                .courses(Set.of(course))
                .build();
    }
}
