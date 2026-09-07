package com.foxminded.schoolmanagementapp.config;

import com.foxminded.schoolmanagementapp.model.Student;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import javax.sql.DataSource;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.tool.schema.Action;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaPersistenceConfiguration {
    private static final String PERSISTENCE_UNIT = "school-management-unit";

    @Bean
    public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
        return new PersistenceConfiguration(PERSISTENCE_UNIT)
                .managedClass(Student.class)
                .property(JdbcSettings.JAKARTA_NON_JTA_DATASOURCE, dataSource)
                .property(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION, Action.ACTION_VALIDATE)
                .property(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
                .property(AvailableSettings.PHYSICAL_NAMING_STRATEGY, PhysicalNamingStrategySnakeCaseImpl.class)
                .property(JdbcSettings.SHOW_SQL, true)
                .property(JdbcSettings.FORMAT_SQL, true)
                .property(JdbcSettings.HIGHLIGHT_SQL, true)
                .createEntityManagerFactory();
    }
}
