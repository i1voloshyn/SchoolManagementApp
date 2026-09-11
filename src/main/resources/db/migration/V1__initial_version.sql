CREATE SEQUENCE group_id_seq INCREMENT BY 50 START WITH 1;
CREATE SEQUENCE student_id_seq INCREMENT BY 50 START WITH 1;
CREATE SEQUENCE course_id_seq INCREMENT BY 50 START WITH 1;

CREATE TABLE groups
(
    id   BIGINT PRIMARY KEY DEFAULT nextval('group_id_seq'),
    name TEXT NOT NULL
);
CREATE TABLE students
(
    id         BIGINT PRIMARY KEY DEFAULT nextval('student_id_seq'),
    group_id   BIGINT references groups (id) ON DELETE SET NULL,
    first_name TEXT   NOT NULL,
    last_name  TEXT   NOT NULL
);
CREATE TABLE courses
(
    id          BIGINT PRIMARY KEY DEFAULT nextval('course_id_seq'),
    name        TEXT UNIQUE NOT NULL,
    description TEXT        NOT NULL
);

CREATE table students_courses
(
    student_id BIGINT references students (id) ON DELETE CASCADE,
    course_id  BIGINT references courses (id) ON DELETE CASCADE,
    PRIMARY KEY (student_id, course_id)
);
