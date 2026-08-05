INSERT INTO groups (group_name)
VALUES ('Group A');

INSERT INTO students (group_id, first_name, last_name)
VALUES ((SELECT group_id FROM groups WHERE group_name = 'Group A'), 'John', 'Smith'),
       ((SELECT group_id FROM groups WHERE group_name = 'Group A'), 'Anna', 'Brown'),
       (NULL, 'Mark', 'Wilson'),
       (NULL, 'Emily', 'Taylor');

INSERT INTO courses (course_name, course_description)
VALUES ('Java', 'Java fundamentals'),
       ('SQL', 'Relational databases and SQL'),
       ('Spring', 'Spring Framework fundamentals');

INSERT INTO students_courses (student_id, course_id)
VALUES ((SELECT student_id FROM students WHERE first_name = 'John'),
        (SELECT course_id FROM courses WHERE course_name = 'Java')),

       ((SELECT student_id FROM students WHERE first_name = 'John'),
        (SELECT course_id FROM courses WHERE course_name = 'SQL')),

       ((SELECT student_id FROM students WHERE first_name = 'Anna'),
        (SELECT course_id FROM courses WHERE course_name = 'Java')),

       ((SELECT student_id FROM students WHERE first_name = 'Mark'),
        (SELECT course_id FROM courses WHERE course_name = 'Spring'));
