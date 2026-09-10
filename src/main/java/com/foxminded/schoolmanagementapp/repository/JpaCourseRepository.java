package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.exception.CourseNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
    public void delete(Long id) {
        emf.runInTransaction(em -> em.createQuery(DELETE_COURSE_QUERY).setParameter("id", id).executeUpdate());
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
            var root = cq.from(Course.class);
            cq.select(root).where(cb.equal(root.get("students").get("id"), studentId));
            return em.createQuery(cq).getResultList();
        });
    }
}
