--limit 3 included

-- 0 students – should be returned
INSERT INTO groups (group_name)
VALUES ('Group A');

-- 1 student – should be returned
INSERT INTO groups (group_name)
VALUES ('Group B');

-- 2 students – should be returned
INSERT INTO groups (group_name)
VALUES ('Group C');

-- 3 students – should be returned (exact limit)
INSERT INTO groups (group_name)
VALUES ('Group D');

-- 4 students — should be skipped
INSERT INTO groups (group_name)
VALUES ('Group E');


INSERT INTO students (group_id, first_name, last_name)
VALUES ((SELECT group_id FROM groups WHERE group_name = 'Group B'),
        'John', 'Smith'),

       ((SELECT group_id FROM groups WHERE group_name = 'Group C'),
        'Anna', 'Brown'),
       ((SELECT group_id FROM groups WHERE group_name = 'Group C'),
        'Mark', 'Wilson'),

       ((SELECT group_id FROM groups WHERE group_name = 'Group D'),
        'Kate', 'Taylor'),
       ((SELECT group_id FROM groups WHERE group_name = 'Group D'),
        'Adam', 'Thomas'),
       ((SELECT group_id FROM groups WHERE group_name = 'Group D'),
        'Emily', 'Jackson'),

       ((SELECT group_id FROM groups WHERE group_name = 'Group E'),
        'Michael', 'White'),
       ((SELECT group_id FROM groups WHERE group_name = 'Group E'),
        'Sarah', 'Harris'),
       ((SELECT group_id FROM groups WHERE group_name = 'Group E'),
        'David', 'Martin'),
       ((SELECT group_id FROM groups WHERE group_name = 'Group E'),
        'Laura', 'Thompson');