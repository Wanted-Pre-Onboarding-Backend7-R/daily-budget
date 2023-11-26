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
        '김철수', 0, 0, '2023-11-22 08:22:53', '2023-11-23 14:47:30');


INSERT INTO expenditure (member_id, category_id, amount, memo, exclude_from_total,
                         expenditure_at, created_at, updated_at)
VALUES (1, 1, 20000, '치킨', 0, '2023-11-20 13:15:42', '2023-11-20 20:36:17', '2023-11-20 20:36:17'),
       (1, 2, 6800, '택시비', 0, '2023-11-21 08:22:35', '2023-11-21 21:20:05', '2023-11-21 21:20:05'),
       (2, 5, 120000, '인프런 강의 구매', 0, '2023-11-21 08:22:35', '2023-11-21 21:20:05', '2023-11-21 21:20:05'),
       (2, 8, 49800, '23년 11월 통신비', 0, '2023-11-22 17:20:05', '2023-11-22 20:48:55', '2023-11-22 20:48:55');