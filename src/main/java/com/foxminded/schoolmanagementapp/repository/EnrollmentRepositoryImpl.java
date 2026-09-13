package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.exception.EnrollmentException;
import com.foxminded.schoolmanagementapp.exception.EnrollmentNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import com.foxminded.schoolmanagementapp.model.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class EnrollmentRepositoryImpl implements EnrollmentRepository {
    private static final String ENROLL_STUDENT_QUERY =
            """
            INSERT INTO students_courses (student_id, course_id)
            VALUES (?, ?)
            """;

    @Value("${spring.jpa.batch.size}")
    private int batchSize;

    @PersistenceContext private EntityManager entityManager;

    @Override
    public void enroll(Long studentId, Long courseId) {
        Course course = entityManager.find(Course.class, courseId);
        if (course == null) {
            throw new EnrollmentException("Course %d does not exist".formatted(courseId));
        }

        Student student = entityManager.find(Student.class, studentId);
        if (student == null) {
            throw new EnrollmentException("Student %d does not exist".formatted(studentId));
        }

        if (!course.addStudent(student)) {
            throw new EnrollmentException(
                    "Student %d is already enrolled in course %d".formatted(studentId, courseId));
        }
    }

    @Override
    public void enrollAll(List<Enrollment> enrollments) {
        if (enrollments.isEmpty()) {
            return;
        }

        try {
            entityManager.flush();
            entityManager
                    .unwrap(Session.class)
                    .doWork(
                            connection -> {
                                try (PreparedStatement statement =
                                        connection.prepareStatement(ENROLL_STUDENT_QUERY)) {
                                    int pendingStatements = 0;

                                    for (Enrollment enrollment : enrollments) {
                                        statement.setLong(1, enrollment.studentId());
                                        statement.setLong(2, enrollment.courseId());
                                        statement.addBatch();
                                        pendingStatements++;

                                        if (pendingStatements == batchSize) {
                                            statement.executeBatch();
                                            statement.clearBatch();
                                            pendingStatements = 0;
                                        }
                                    }

                                    if (pendingStatements > 0) {
                                        statement.executeBatch();
                                    }
                                }
                            });
        } catch (DataIntegrityViolationException | ConstraintViolationException exception) {
            log.warn("Batch enrollment rejected", exception);
            throw new EnrollmentException("Cannot create enrollment batch", exception);
        }
    }

    @Override
    public void removeEnrollment(Long studentId, Long courseId) {
        Course course = entityManager.find(Course.class, courseId);
        Student student = entityManager.find(Student.class, studentId);

        if (course == null || student == null || !course.getStudents().remove(student)) {
            throw new EnrollmentNotFoundException(studentId, courseId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean enrollmentExist(Long studentId, Long courseId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);

        Root<Student> student = query.from(Student.class);
        Join<Student, Course> course = student.join("courses");
        Predicate studentPredicate = criteriaBuilder.equal(student.get("id"), studentId);
        Predicate coursePredicate = criteriaBuilder.equal(course.get("id"), courseId);

        query.select(criteriaBuilder.count(student)).where(studentPredicate, coursePredicate);

        return entityManager.createQuery(query).getSingleResult() > 0;
    }
}
