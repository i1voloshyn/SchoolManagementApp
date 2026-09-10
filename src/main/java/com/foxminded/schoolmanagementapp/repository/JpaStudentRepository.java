package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.exception.StudentNotFoundException;
import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaStudentRepository implements StudentsRepository {
    @Value("${spring.jpa.batch.size:30}")
    private int batchSize;

    private static final String FIND_ALL_STUDENTS_QUERY = "SELECT s  FROM Student s";

    private static final String FIND_STUDENTS_BY_LAST_NAME_QUERY =
            """
                    SELECT s
                    FROM Student s
                    WHERE s.lastName = :last_name
                    """;

    private static final String FIND_STUDENTS_BY_COURSE_NAME =
            """
                     SELECT s
                     FROM Student s
                        JOIN s.courses c
                        WHERE c.name = :course_name
                    """;

    private final EntityManagerFactory emf;


    @Override
    public Student save(Student student) {
        return emf.callInTransaction(em -> {
            em.persist(student);
            return student;
        });
    }

    @Override
    public List<Student> saveAll(List<Student> students) {
        if (students.isEmpty()) {
            return List.of();
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (int i = 0; i < students.size(); i++) {
                em.persist(students.get(i));
                if ((i + 1) % batchSize == 0) {
                    em.flush();
                    em.clear();
                }
            }
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
        return students;
    }


    @Override
    public void delete(Long id) {
        emf.runInTransaction(em -> {
            Student st = em.find(Student.class, id);
            if (st == null) {
                throw new StudentNotFoundException(id);
            }
            em.remove(st);
        });
    }

    @Override
    public List<Student> findAll() {
        return emf.callInTransaction(em ->
                em.createQuery(FIND_ALL_STUDENTS_QUERY, Student.class).getResultList());
    }

    @Override
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(emf.callInTransaction(em -> em.find(Student.class, id)));
    }

    @Override
    public List<Student> findByLastName(String lastName) {
        return emf.callInTransaction(em ->
                {
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<Student> cq = cb.createQuery(Student.class);
                    var root = cq.from(Student.class);

                    cq.select(root).where(root.get("lastName").equalTo(lastName));
                    return em.createQuery(cq).getResultList();
                }
        );
    }

    @Override
    public List<Student> findByCourseName(String courseName) {
        return emf.callInTransaction(em ->
                {
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<Student> cq = cb.createQuery(Student.class);
                    var root = cq.from(Student.class);

                    Join<Student, Course> courseJoin = root.join("courses");
                    Predicate coursePredicate = cb.equal(courseJoin.get("name"), courseName);

                    cq.select(root).where(coursePredicate);
                    return em.createQuery(cq).getResultList();
                }
        );
    }

}
