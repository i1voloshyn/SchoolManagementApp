INSERT INTO groups (name)
VALUES ('Group A');

INSERT INTO students (group_id, first_name, last_name)
VALUES ((SELECT id FROM groups WHERE name = 'Group A'), 'John', 'Smith'),
       ((SELECT id FROM groups WHERE name = 'Group A'), 'Anna', 'Brown'),
       (NULL, 'Mark', 'Wilson'),
       (NULL, 'Emily', 'Taylor');

INSERT INTO courses (name, description)
VALUES ('Java', 'Java fundamentals'),
       ('SQL', 'Relational databases and SQL'),
       ('Spring', 'Spring Framework fundamentals');