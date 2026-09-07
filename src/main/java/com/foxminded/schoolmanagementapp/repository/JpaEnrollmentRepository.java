package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.exception.EnrollmentException;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaEnrollmentRepository implements EnrollmentRepository {
    @Value("${spring.jpa.batch.size:30}")
    private int batchSize;

    private static final String ENROLL_STUDENT_QUERY =
            """
                    INSERT INTO students_courses (student_id, course_id)
                    VALUES (:student_id, :course_id)
                    """;
    private static final String REMOVE_STUDENT_QUERY =
            """
                    DELETE FROM students_courses
                    WHERE student_id = :student_id
                      AND course_id = :course_id
                    """;
    private static final String ENROLLMENT_EXISTS_QUERY =
            """
                    SELECT EXISTS (
                        SELECT 1
                        FROM students_courses
                        WHERE student_id = :student_id
                          AND course_id = :course_id
                    )
                    """;

    private final EntityManagerFactory emf;

    @Override
    public void enroll(Long studentId, Long courseId) {
        try {
            emf.runInTransaction(em -> em.createNativeQuery(ENROLL_STUDENT_QUERY)
                    .setParameter("student_id", studentId)
                    .setParameter("course_id", courseId)
                    .executeUpdate());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.warn("Enrollment rejected:studentId = {}, courseId = {}", studentId, courseId, e);

            throw new EnrollmentException(
                    "Cannot enroll student %d in course %d".formatted(studentId, courseId), e);
        }

    }

    @Override
    public void enrollAll(List<Enrollment> enrollments) {
        if (enrollments.isEmpty()) {
            return;
        }

        try {
            emf.runInTransaction(em -> {
                for (int i = 0; i < enrollments.size(); i++) {
                    Enrollment enrollment = enrollments.get(i);
                    em.createNativeQuery(ENROLL_STUDENT_QUERY)
                            .setParameter("student_id", enrollment.studentId())
                            .setParameter("course_id", enrollment.courseId())
                            .executeUpdate();

                    if (i > 0 && i % batchSize == 0) {
                        em.flush();
                        em.clear();
                    }
                }
            });
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.warn("Batch enrollment rejected", e);

            throw new EnrollmentException("Cannot create enrollment batch", e);
        }
    }

    @Override
    public void remove(Long studentId, Long courseId) {
        emf.runInTransaction(em -> {
            em.createNativeQuery(REMOVE_STUDENT_QUERY)
                    .setParameter("student_id", studentId)
                    .setParameter("course_id", courseId)
                    .executeUpdate();
        });
    }

    @Override
    public boolean exists(Long studentId, Long courseId) {
        return emf.callInTransaction(
                em -> (Boolean) em.createNativeQuery(ENROLLMENT_EXISTS_QUERY, Boolean.class)
                        .setParameter("student_id", studentId)
                        .setParameter("course_id", courseId)
                        .getSingleResult());
    }

}
