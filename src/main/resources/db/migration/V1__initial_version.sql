CREATE TABLE groups
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL
);
CREATE TABLE students
(
    id         BIGSERIAL PRIMARY KEY,
    group_id   INTEGER references groups (id) ON DELETE SET NULL,
    first_name TEXT    NOT NULL,
    last_name  TEXT    NOT NULL
);
CREATE TABLE courses
(
    id          BIGSERIAL PRIMARY KEY,
    name        TEXT UNIQUE NOT NULL,
    description TEXT        NOT NULL
);

CREATE table students_courses
(
    student_id BIGINT references students (id) ON DELETE CASCADE,
    course_id  BIGINT references courses (id) ON DELETE CASCADE,
    PRIMARY KEY (student_id, course_id)
);
