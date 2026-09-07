package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Course;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaCourseRepository implements CourseRepository {

    private static final String DELETE_COURSE_QUERY = "DELETE FROM Course c WHERE c.id = :id";
    private static final String FIND_ALL_COURSES_QUERY = "SELECT c FROM Course c";
    private static final String FIND_COURSE_BY_ID_QUERY = "SELECT c FROM Course c WHERE c.id = :id";
    private static final String FIND_COURSES_BY_NAME_QUERY = "SELECT c FROM Course c WHERE c.name = :name";
    private static final String FIND_COURSES_BY_STUDENT_ID_QUERY =
            """
                    SELECT c FROM Course c
                    JOIN c.students s
                    WHERE s.id = :student_id
                    """;

    private final EntityManagerFactory emf;

    @Override
    public Course save(Course course) {
        return emf.callInTransaction(em -> {
            em.persist(course);
            return course;
        });
    }

    @Override
    public void delete(Long id) {
        emf.runInTransaction(em -> em.createQuery(DELETE_COURSE_QUERY)
                .setParameter("id", id)
                .executeUpdate()
        );
    }

    @Override
    public List<Course> findAll() {
        return emf.callInTransaction(em -> em.createQuery(FIND_ALL_COURSES_QUERY, Course.class).getResultList());
    }

    @Override
    public Optional<Course> findById(Long id) {
        return emf.callInTransaction(em -> em.createQuery(FIND_COURSE_BY_ID_QUERY, Course.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst());
    }

    @Override
    public Optional<Course> findByName(String name) {
        return emf.callInTransaction(em -> em.createQuery(FIND_COURSES_BY_NAME_QUERY, Course.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst());
    }

    @Override
    public List<Course> findByStudentId(Long studentId) {
        return emf.callInTransaction(em -> em.createQuery(FIND_COURSES_BY_STUDENT_ID_QUERY, Course.class)
                .setParameter("student_id", studentId)
                .getResultList());
    }

}
