package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.exception.EnrollmentException;
import com.foxminded.schoolmanagementapp.exception.EnrollmentNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Enrollment;
import com.foxminded.schoolmanagementapp.model.Student;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaCourseRepository implements CourseRepository {
    private static final String ENROLL_STUDENT_QUERY =
            """
                    INSERT INTO students_courses (student_id, course_id)
                    VALUES (?, ?)
                    """;

    @Value("${spring.jpa.batch.size:30}")
    private int batchSize;

    private final EntityManagerFactory emf;

    @Override
    public Course save(Course course) {
        return emf.callInTransaction(em -> {
            em.persist(course);
            return course;
        });
    }

    @Override
    public Course update(Course course) {
        return emf.callInTransaction(em -> {
            Course managed = em.find(Course.class, course.getId());
            if (managed == null) {
                throw new CourseNotFoundException(course.getId());
            }

            managed.setName(course.getName());
            managed.setDescription(course.getDescription());
            return managed;
        });
    }


    @Override
    public void enroll(Long studentId, Long courseId) {
        emf.runInTransaction(em -> {
            Course course = em.find(Course.class, courseId);
            if (course == null) {
                throw new EnrollmentException("Course %d does not exist".formatted(courseId));
            }

            Student student = em.find(Student.class, studentId);
            if (student == null) {
                throw new EnrollmentException("Student %d does not exist".formatted(studentId));
            }

            if (!course.addStudent(student)) {
                throw new EnrollmentException(
                        "Student %d is already enrolled in course %d"
                                .formatted(studentId, courseId));
            }
        });
    }

    @Override
    public void enrollAll(List<Enrollment> enrollments) {
        if (enrollments.isEmpty()) {
            return;
        }

        try {
            emf.runInTransaction(
                    em -> em.unwrap(Session.class)
                            .doWork(connection -> {
                                try (PreparedStatement statement =
                                             connection.prepareStatement(
                                                     ENROLL_STUDENT_QUERY)) {
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
                            }));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.warn("Batch enrollment rejected", e);

            throw new EnrollmentException("Cannot create enrollment batch", e);
        }
    }


    @Override
    public void removeEnrollment(Long studentId, Long courseId) {
        emf.runInTransaction(em -> {
            Course course = em.find(Course.class, courseId);
            Student student = em.find(Student.class, studentId);

            if (course == null || student == null || !course.getStudents().remove(student)) {
                throw new EnrollmentNotFoundException(studentId, courseId);
            }
        });
    }

    @Override
    public boolean enrollmentExist(Long studentId, Long courseId) {
        return emf.callInTransaction(
                em ->
                {
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<Long> cq = cb.createQuery(Long.class);

                    Root<Student> student = cq.from(Student.class);
                    Join<Student, Course> courseJoin = student.join("courses");

                    Predicate studentPredicate = cb.equal(student.get("id"), studentId);
                    Predicate coursePredicate = cb.equal(courseJoin.get("id"), courseId);

                    cq.select(cb.count(student)).where(studentPredicate, coursePredicate);

                    Long count = em.createQuery(cq).getSingleResult();
                    return count > 0;
                }
        );
    }

    @Override
    public void delete(Long id) {
        emf.runInTransaction(em ->
        {
            Course course = em.find(Course.class, id);
            if (course == null) {
                throw new CourseNotFoundException(id);
            }
            em.remove(course);
        });
    }

    @Override
    public List<Course> findAll() {
        return emf.callInTransaction(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Course> cq = cb.createQuery(Course.class);
            var root = cq.from(Course.class);
            cq.select(root);
            return em.createQuery(cq).getResultList();
        });
    }

    @Override
    public Optional<Course> findById(Long id) {
        return emf.callInTransaction(em -> Optional.ofNullable(em.find(Course.class, id)));
    }

    @Override
    public Optional<Course> findByName(String name) {
        return emf.callInTransaction(em -> {
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<Course> cq = cb.createQuery(Course.class);
                    var root = cq.from(Course.class);
                    cq.select(root).where(cb.equal(root.get("name"), name));
                    List<Course> courses = em.createQuery(cq).getResultList();
                    return courses.stream().findFirst();
                }
        );
    }

    @Override
    public List<Course> findByStudentId(Long studentId) {
        return emf.callInTransaction(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Course> cq = cb.createQuery(Course.class);
            Root<Course> course = cq.from(Course.class);
            Join<Course, Student> students = course.join("students");

            cq.select(course)
                    .where(cb.equal(students.get("id"), studentId))
                    .distinct(true);

            return em.createQuery(cq).getResultList();
        });
    }
}
