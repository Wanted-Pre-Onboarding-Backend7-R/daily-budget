set FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE category;
TRUNCATE TABLE member;
TRUNCATE TABLE expenditure;
set FOREIGN_KEY_CHECKS = 1;

INSERT INTO category (type)
VALUES ('FOOD'),           -- 1
       ('TRANSPORTATION'), -- 2
       ('SHOPPING'),       -- 3
       ('MEDICAL'),        -- 4
       ('EDUCATION'),      -- 5
       ('ENTERTAINMENT'),  -- 6
       ('FINANCE'),        -- 7
       ('PHONE'),          -- 8
       ('HOUSE'),          -- 9
       ('UNCATEGORIZED'); -- 10

INSERT INTO member (account_id, password, role, name, allow_daily_budget_noti,
                    allow_daily_expenditure_noti, created_at, updated_at)
VALUES ('member1', '$2a$10$x6a5r29ElOc5nThogQozT.ZZijwjZKEODkl52ayATgVtMm5gbe4Zy', 'ROLE_USER',
        '홍길동', 0, 0, '2023-11-17 15:07:23', '2023-11-19 16:25:22'),
       ('member2', '$2a$10$559fNXUZW3MFH2IUEACAn.Xm.LXnPHs25rySDLbj1oxKQ2VAavIEu', 'ROLE_USER',
        '김철수', 0, 0, '2023-11-22 08:22:53', '2023-11-23 14:47:30'),
       ('member3', '$2a$10$HKlWvxQ0Ce/w3gBRjbHEiOlLT7AgzoV60OH.8J3gJUhq1NhtdKRri', 'ROLE_USER',
        '이개발', 0, 0, '2023-11-23 13:47:35', '2023-11-23 13:47:35'),
       ('member4', '$2a$10$zeI4mAQy2Xn4Z8rP/cxqguSAB/t7.5I9oTKdqc2BU1BrbzJ/7JhuS', 'ROLE_USER',
        '박명수', 0, 0, '2023-11-24 16:05:12', '2023-11-26 12:52:33'),
       ('member5', '$2a$10$hIJnOOCZ6HjpLOTXaE3b2eZhmFGTkaFERctDrHpT4rRksrtsSyuiG', 'ROLE_USER',
        '정형돈', 0, 0, '2023-11-26 20:12:55', '2023-11-26 20:33:46');

INSERT INTO expenditure (member_id, category_id, amount, memo, exclude_from_total,
                         expenditure_at, created_at, updated_at)
VALUES
    -- member1
    -- FOOD category
    (1, 1, 10000, '음식 1', false, '2023-11-02T11:30:00', NOW(), NOW()),
    (1, 1, 20000, '음식 2', false, '2023-11-02T12:30:00', NOW(), NOW()),
    (1, 1, 30000, '음식 3', false, '2023-11-02T13:30:00', NOW(), NOW()),
    (1, 1, 40000, '음식 4', false, '2023-11-02T14:30:00', NOW(), NOW()),
    (1, 1, 50000, '음식 5', false, '2023-11-02T15:30:00', NOW(), NOW()),

    -- EDUCATION category
    (1, 5, 120000, '강의 구매 6', false, '2023-11-03T16:30:00', NOW(), NOW()),
    (1, 5, 140000, '강의 구매 7', false, '2023-11-03T17:30:00', NOW(), NOW()),

    -- member2
    -- UNCATEGORIZED category
    (2, 10, 80000, '기타 8', false, '2023-11-06T18:30:00', NOW(), NOW()),
    (2, 10, 90000, '기타 9', false, '2023-11-06T19:30:00', NOW(), NOW()),

    -- UNCATEGORIZED category, excludeFromTotal true
    (2, 10, 100000, '기타 10', true, '2023-11-07T20:30:00', NOW(), NOW()),
    (2, 10, 110000, '기타 11', true, '2023-11-07T21:30:00', NOW(), NOW()),

    -- SHOPPING category
    (2, 3, 12000, '쇼핑 12', false, '2023-11-09T10:12:00', NOW(), NOW()),
    (2, 3, 13000, '쇼핑 13', false, '2023-11-09T10:13:00', NOW(), NOW()),
    (2, 3, 14000, '쇼핑 14', false, '2023-11-09T10:14:00', NOW(), NOW()),
    (2, 3, 15000, '쇼핑 15', false, '2023-11-09T10:15:00', NOW(), NOW());