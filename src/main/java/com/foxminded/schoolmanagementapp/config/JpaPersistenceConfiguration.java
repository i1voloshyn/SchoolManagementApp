package com.foxminded.schoolmanagementapp.config;

import com.foxminded.schoolmanagementapp.model.Course;
import com.foxminded.schoolmanagementapp.model.Group;
import com.foxminded.schoolmanagementapp.model.Student;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.BatchSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.tool.schema.Action;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class JpaPersistenceConfiguration {
    private static final String PERSISTENCE_UNIT = "school-management-unit";

    @Value("${spring.jpa.batch.size:30}")
    private int BATCH_SIZE;

    @Bean
    public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
        return new PersistenceConfiguration(PERSISTENCE_UNIT)
                .managedClass(Student.class)
                .managedClass(Group.class)
                .managedClass(Course.class)
                .property(JdbcSettings.JAKARTA_NON_JTA_DATASOURCE, dataSource)
                .property(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION, Action.VALIDATE)
                .property(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
                .property(AvailableSettings.PHYSICAL_NAMING_STRATEGY, PhysicalNamingStrategySnakeCaseImpl.class)
                .property(BatchSettings.STATEMENT_BATCH_SIZE, BATCH_SIZE)
                .property(JdbcSettings.SHOW_SQL, true)
                .property(JdbcSettings.FORMAT_SQL, true)
                .property(JdbcSettings.HIGHLIGHT_SQL, true)
                .createEntityManagerFactory();
    }
}
