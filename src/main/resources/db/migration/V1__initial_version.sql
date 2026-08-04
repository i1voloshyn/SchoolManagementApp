CREATE TABLE groups
(
    group_id   BIGSERIAL PRIMARY KEY,
    group_name TEXT NOT NULL
);
CREATE TABLE students
(
    student_id BIGSERIAL PRIMARY KEY,
    group_id   INTEGER references groups (group_id) ON DELETE SET NULL,
    first_name TEXT    NOT NULL,
    last_name  TEXT    NOT NULL
);
CREATE TABLE courses
(
    course_id          BIGSERIAL PRIMARY KEY,
    course_name        TEXT NOT NULL,
    course_description TEXT NOT NULL
);

CREATE table students_courses
(
    student_id BIGINT references students (student_id) ON DELETE CASCADE,
    course_id  BIGINT references courses (course_id) ON DELETE CASCADE,
    PRIMARY KEY (student_id, course_id)
);
