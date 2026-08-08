--limit 3 included

-- 0 students – should be returned
INSERT INTO groups (name)
VALUES ('Group A');

-- 1 student – should be returned
INSERT INTO groups (name)
VALUES ('Group B');

-- 2 students – should be returned
INSERT INTO groups (name)
VALUES ('Group C');

-- 3 students – should be returned (exact limit)
INSERT INTO groups (name)
VALUES ('Group D');

-- 4 students — should be skipped
INSERT INTO groups (name)
VALUES ('Group E');


INSERT INTO students (group_id, first_name, last_name)
VALUES ((SELECT id FROM groups WHERE name = 'Group B'),
        'John', 'Smith'),

       ((SELECT id FROM groups WHERE name = 'Group C'),
        'Anna', 'Brown'),
       ((SELECT id FROM groups WHERE name = 'Group C'),
        'Mark', 'Wilson'),

       ((SELECT id FROM groups WHERE name = 'Group D'),
        'Kate', 'Taylor'),
       ((SELECT id FROM groups WHERE name = 'Group D'),
        'Adam', 'Thomas'),
       ((SELECT id FROM groups WHERE name = 'Group D'),
        'Emily', 'Jackson'),

       ((SELECT id FROM groups WHERE name = 'Group E'),
        'Michael', 'White'),
       ((SELECT id FROM groups WHERE name = 'Group E'),
        'Sarah', 'Harris'),
       ((SELECT id FROM groups WHERE name = 'Group E'),
        'David', 'Martin'),
       ((SELECT id FROM groups WHERE name = 'Group E'),
        'Laura', 'Thompson');