INSERT INTO groups (group_name)
VALUES ('Group A'),
       ('Group B');

INSERT INTO students (group_id, first_name, last_name)
VALUES ((SELECT group_id FROM groups WHERE group_name = 'Group A'), 'John', 'Smith'),
       ((SELECT group_id FROM groups WHERE group_name = 'Group A'), 'Anna', 'Smith'),
       ((SELECT group_id FROM groups WHERE group_name = 'Group B'), 'Mark', 'Brown'),
       ((SELECT group_id FROM groups WHERE group_name = 'Group B'), 'Kate', 'Taylor'),
       (NULL, 'Emily', 'Wilson');
