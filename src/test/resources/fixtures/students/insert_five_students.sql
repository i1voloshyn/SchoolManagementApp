INSERT INTO groups (name)
VALUES ('Group A'),
       ('Group B');

INSERT INTO students (group_id, first_name, last_name)
VALUES ((SELECT id FROM groups WHERE name = 'Group A'), 'John', 'Smith'),
       ((SELECT id FROM groups WHERE name = 'Group A'), 'Anna', 'Smith'),
       ((SELECT id FROM groups WHERE name = 'Group B'), 'Mark', 'Brown'),
       ((SELECT id FROM groups WHERE name = 'Group B'), 'Kate', 'Taylor'),
       (NULL, 'Emily', 'Wilson');
