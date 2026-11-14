# School Management App

### A console application for managing school data

**School Management App** is a Java and Spring Boot console application for working with students, groups, courses, and student-course enrolments. It stores data in PostgreSQL and provides an interactive command-line menu for common school-management operations.

The project was created as a practical introduction to the Spring ecosystem. Its purpose is not only to build a working application, but also to learn persistence step by step: beginning with low-level database access through Spring JDBC, progressing to JPA and Hibernate with `EntityManager`, and finally adopting Spring Data JPA repositories. This makes the repository a record of the migration path as well as the finished application.

> **Project status:** The current implementation uses Spring Data JPA for persistence and is intended as an educational project.

## Current Features

- Manages students, groups, courses, and student-course enrolments.
- Provides an interactive console menu to:
  - find groups with no more than a specified number of students;
  - find students enrolled in a course;
  - view all groups and courses;
  - create and delete students;
  - add or remove a student from a course; and
  - update a course.
- Creates the database schema through Flyway migrations.
- Generates initial groups, courses, students, and enrolments when the database is empty.
- Uses validation, domain-specific exceptions, and logging for console operations.
- Includes unit, migration, and JPA repository tests.

## Technology Stack

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot |
| Persistence | Spring Data JPA, JPA / Hibernate |
| Database | PostgreSQL |
| Database migrations | Flyway |
| Object mapping | MapStruct |
| Test data | DataFaker |
| Testing | JUnit 5, Mockito, AssertJ, Testcontainers |
| Build and development | Maven, Maven Wrapper, Docker Compose |
| Code style | Spotless with Google Java Format (AOSP style) |

## Learning Path

This repository intentionally evolves the persistence layer instead of introducing every Spring abstraction at once.

1. **Database schema and migrations** — PostgreSQL tables and relationships are defined with Flyway.
2. **Spring JDBC** — repositories were first implemented with explicit SQL and JDBC-based data access.
3. **Service and application layers** — business operations, DTO mapping, data generation, a console UI, validation, and logging were added around the persistence layer.
4. **JPA and Hibernate** — the JDBC implementation was migrated to entities and `EntityManager`, including relationship mapping and sequence-based identifiers.
5. **Spring Data JPA** — repositories were then simplified with `JpaRepository`, derived query methods, JPQL, and native queries where appropriate.

The commit history preserves these stages, making it possible to compare the approaches and understand why higher-level Spring abstractions are useful.

## Getting Started

### Prerequisites

- Java 21
- Docker and Docker Compose

### Run locally

1. Clone the repository and enter its directory.

   ```bash
   git clone https://github.com/i1voloshyn/SchoolManagementApp.git
   cd SchoolManagementApp
   ```

2. Create a `.env` file in the project root with credentials for PostgreSQL:

   ```properties
   DB_USERNAME=postgres
   DB_PASSWORD=postgres
   ```

3. Start PostgreSQL:

   ```bash
   docker compose up -d
   ```

4. Start the application:

   ```bash
   ./mvnw spring-boot:run
   ```

   On Windows, use `mvnw.cmd spring-boot:run`.

Flyway applies the schema migration on startup. When the database has no application data, the application generates sample groups, courses, students, and enrolments before showing the console menu.

### Run tests

```bash
./mvnw test
```

Repository and migration tests use Testcontainers, so a running Docker environment is required.

## About This Repository

This repository is a personal educational project for learning Java persistence and the Spring ecosystem through gradual, hands-on migration. The current code demonstrates the final Spring Data JPA-based version, while the commit history documents its evolution from Spring JDBC through JPA/Hibernate to Spring Data JPA.
