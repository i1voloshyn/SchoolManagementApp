package com.foxminded.schoolmanagementapp.repository;

import com.foxminded.schoolmanagementapp.model.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaStudentRepository implements StudentsRepository {
    @Value("${spring.jpa.batch.size:30}")
    private int BATCH_SIZE;

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
                if (i > 0 && i % BATCH_SIZE == 0) {
                    em.flush();
                    em.clear();
                }
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.getTransaction().commit();
            em.close();
        }
        return students;
    }


    @Override
    public void delete(Long id) {
        emf.runInTransaction(em -> {
            em.createQuery("DELETE FROM Student s WHERE s.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        });
    }

    @Override
    public List<Student> findAll() {
        return emf.callInTransaction(em -> em.createQuery(FIND_ALL_STUDENTS_QUERY, Student.class).getResultList());
    }

    @Override
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(emf.callInTransaction(em -> em.find(Student.class, id)));
    }

    @Override
    public List<Student> findByLastName(String lastName) {
        return emf.callInTransaction(em -> em.createQuery(FIND_STUDENTS_BY_LAST_NAME_QUERY, Student.class)
                .setParameter("last_name", lastName)
                .getResultList());
    }

    @Override
    public List<Student> findByCourseName(String courseName) {
        return emf.callInTransaction(em -> em.createQuery(FIND_STUDENTS_BY_COURSE_NAME, Student.class)
                .setParameter("course_name", courseName)
                .getResultList());
    }

}
