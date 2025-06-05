create schema if not exists hrbank;

GRANT ALL PRIVILEGES ON SCHEMA hrbank TO fource;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA hrbank TO fource;

ALTER ROLE fource SET search_path TO hrbank, public;

show search_path;

-- 부서 테이블
CREATE TABLE tbl_department
(
    id               SERIAL PRIMARY KEY,
    name             VARCHAR     NOT NULL,
    description      VARCHAR     NOT NULL,
    established_date DATE        NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ
);

-- 파일 메타데이터 테이블
CREATE TABLE tbl_file_metadata
(
    id           SERIAL PRIMARY KEY,
    created_at   TIMESTAMPTZ NOT NULL,
    file_name    VARCHAR     NOT NULL,
    content_type VARCHAR     NOT NULL,
    size         BIGINT      NOT NULL
);

-- 직원 테이블
CREATE TABLE tbl_employees
(
    id               SERIAL PRIMARY KEY,
    profile_image_id INT             NOT NULL,
    department_id    INT             NOT NULL,
    name             VARCHAR         NOT NULL,
    email            VARCHAR         NOT NULL,
    employee_number  VARCHAR         NOT NULL,
    position         VARCHAR         NOT NULL,
    hire_date        DATE            NOT NULL,
    status           VARCHAR         NOT NULL,
    created_at       TIMESTAMPTZ     NOT NULL,
    updated_at       TIMESTAMPTZ
);

-- 변경 이력 로그 테이블
CREATE TABLE tbl_change_log
(
    id          SERIAL PRIMARY KEY,
    employee_id INT         NOT NULL,
    changed_at  TIMESTAMPTZ NOT NULL,
    changed_ip  VARCHAR(50) NOT NULL,
    type        VARCHAR     NOT NULL,
    memo        TEXT,
    created_at  TIMESTAMPTZ NOT NULL
);

-- 변경 상세 이력 테이블
CREATE TABLE tbl_change_detail
(
    id            SERIAL PRIMARY KEY,
    change_log_id INT         NOT NULL,
    field_name    VARCHAR     NOT NULL,
    old_value     VARCHAR     NOT NULL,
    new_value     VARCHAR     NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL
);

-- 백업 기록 테이블
CREATE TABLE tbl_backup_history
(
    id         SERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ   NOT NULL,
    worker     VARCHAR       NOT NULL,
    started_at TIMESTAMPTZ   NOT NULL,
    ended_at   TIMESTAMPTZ   NOT NULL,
    status     VARCHAR       NOT NULL,
    file_id    INT           NOT NULL
);

-- FK 제약 조건
ALTER TABLE tbl_employees
    ADD CONSTRAINT fk_employees_profile_image
        FOREIGN KEY (profile_image_id)
            REFERENCES tbl_file_metadata(id)
            on delete set null;

ALTER TABLE tbl_employees
    ADD CONSTRAINT fk_employees_department
        FOREIGN KEY (department_id)
            REFERENCES tbl_department(id);

ALTER TABLE tbl_change_log
    ADD CONSTRAINT fk_change_log_employee
        FOREIGN KEY (employee_id)
            REFERENCES tbl_employees(id);

ALTER TABLE tbl_backup_history
    ADD CONSTRAINT fk_backup_history_file
        FOREIGN KEY (file_id)
            REFERENCES tbl_file_metadata(id);

ALTER TABLE tbl_change_detail
    ADD CONSTRAINT fk_change_detail_log
        FOREIGN KEY (change_log_id)
            REFERENCES tbl_change_log(id)
            ON DELETE CASCADE;

-- tbl_department
INSERT INTO tbl_department VALUES (1, '개발본부', '서비스 개발', '2020-01-01', NOW(), NOW());
INSERT INTO tbl_department VALUES (2, '기획팀', '신규 기획', '2020-01-02', NOW(), NOW());
INSERT INTO tbl_department VALUES (3, '디자인팀', 'UX/UI 디자인', '2020-01-03', NOW(), NOW());
INSERT INTO tbl_department VALUES (4, '운영팀', '서비스 운영', '2020-01-04', NOW(), NOW());
INSERT INTO tbl_department VALUES (5, '마케팅팀', '브랜드 마케팅', '2020-01-05', NOW(), NOW());

-- tbl_file_metadata
INSERT INTO tbl_file_metadata VALUES (1, NOW(), 'profile_1.jpg', 'image/jpeg', 39790);
INSERT INTO tbl_file_metadata VALUES (2, NOW(), 'profile_2.jpg', 'image/jpeg', 23845);
INSERT INTO tbl_file_metadata VALUES (3, NOW(), 'profile_3.jpg', 'image/jpeg', 33764);
INSERT INTO tbl_file_metadata VALUES (4, NOW(), 'profile_4.jpg', 'image/jpeg', 22679);
INSERT INTO tbl_file_metadata VALUES (5, NOW(), 'profile_5.jpg', 'image/jpeg', 28503);
INSERT INTO tbl_file_metadata VALUES (6, NOW(), 'profile_6.jpg', 'image/jpeg', 18647);
INSERT INTO tbl_file_metadata VALUES (7, NOW(), 'profile_7.jpg', 'image/jpeg', 37419);
INSERT INTO tbl_file_metadata VALUES (8, NOW(), 'profile_8.jpg', 'image/jpeg', 34647);
INSERT INTO tbl_file_metadata VALUES (9, NOW(), 'profile_9.jpg', 'image/jpeg', 34108);
INSERT INTO tbl_file_metadata VALUES (10, NOW(), 'profile_10.jpg', 'image/jpeg', 37896);
INSERT INTO tbl_file_metadata VALUES (11, NOW(), 'profile_11.jpg', 'image/jpeg', 24976);
INSERT INTO tbl_file_metadata VALUES (12, NOW(), 'profile_12.jpg', 'image/jpeg', 39848);
INSERT INTO tbl_file_metadata VALUES (13, NOW(), 'profile_13.jpg', 'image/jpeg', 17064);
INSERT INTO tbl_file_metadata VALUES (14, NOW(), 'profile_14.jpg', 'image/jpeg', 19135);
INSERT INTO tbl_file_metadata VALUES (15, NOW(), 'profile_15.jpg', 'image/jpeg', 31315);
INSERT INTO tbl_file_metadata VALUES (16, NOW(), 'profile_16.jpg', 'image/jpeg', 27906);
INSERT INTO tbl_file_metadata VALUES (17, NOW(), 'profile_17.jpg', 'image/jpeg', 25074);
INSERT INTO tbl_file_metadata VALUES (18, NOW(), 'profile_18.jpg', 'image/jpeg', 31306);
INSERT INTO tbl_file_metadata VALUES (19, NOW(), 'profile_19.jpg', 'image/jpeg', 35064);
INSERT INTO tbl_file_metadata VALUES (20, NOW(), 'profile_20.jpg', 'image/jpeg', 22944);
INSERT INTO tbl_file_metadata VALUES (21, NOW(), 'profile_21.jpg', 'image/jpeg', 39811);
INSERT INTO tbl_file_metadata VALUES (22, NOW(), 'profile_22.jpg', 'image/jpeg', 37072);
INSERT INTO tbl_file_metadata VALUES (23, NOW(), 'profile_23.jpg', 'image/jpeg', 30616);
INSERT INTO tbl_file_metadata VALUES (24, NOW(), 'profile_24.jpg', 'image/jpeg', 26209);
INSERT INTO tbl_file_metadata VALUES (25, NOW(), 'profile_25.jpg', 'image/jpeg', 28494);
INSERT INTO tbl_file_metadata VALUES (26, NOW(), 'profile_26.jpg', 'image/jpeg', 38780);
INSERT INTO tbl_file_metadata VALUES (27, NOW(), 'profile_27.jpg', 'image/jpeg', 22628);
INSERT INTO tbl_file_metadata VALUES (28, NOW(), 'profile_28.jpg', 'image/jpeg', 16167);
INSERT INTO tbl_file_metadata VALUES (29, NOW(), 'profile_29.jpg', 'image/jpeg', 36933);
INSERT INTO tbl_file_metadata VALUES (30, NOW(), 'profile_30.jpg', 'image/jpeg', 36808);
INSERT INTO tbl_file_metadata VALUES (31, NOW(), 'profile_31.jpg', 'image/jpeg', 26526);
INSERT INTO tbl_file_metadata VALUES (32, NOW(), 'profile_32.jpg', 'image/jpeg', 21314);
INSERT INTO tbl_file_metadata VALUES (33, NOW(), 'profile_33.jpg', 'image/jpeg', 21234);
INSERT INTO tbl_file_metadata VALUES (34, NOW(), 'profile_34.jpg', 'image/jpeg', 25451);
INSERT INTO tbl_file_metadata VALUES (35, NOW(), 'profile_35.jpg', 'image/jpeg', 19496);
INSERT INTO tbl_file_metadata VALUES (36, NOW(), 'profile_36.jpg', 'image/jpeg', 38105);
INSERT INTO tbl_file_metadata VALUES (37, NOW(), 'profile_37.jpg', 'image/jpeg', 20949);
INSERT INTO tbl_file_metadata VALUES (38, NOW(), 'profile_38.jpg', 'image/jpeg', 38256);
INSERT INTO tbl_file_metadata VALUES (39, NOW(), 'profile_39.jpg', 'image/jpeg', 19192);
INSERT INTO tbl_file_metadata VALUES (40, NOW(), 'profile_40.jpg', 'image/jpeg', 34387);
INSERT INTO tbl_file_metadata VALUES (41, NOW(), 'profile_41.jpg', 'image/jpeg', 24426);
INSERT INTO tbl_file_metadata VALUES (42, NOW(), 'profile_42.jpg', 'image/jpeg', 19508);
INSERT INTO tbl_file_metadata VALUES (43, NOW(), 'profile_43.jpg', 'image/jpeg', 16377);
INSERT INTO tbl_file_metadata VALUES (44, NOW(), 'profile_44.jpg', 'image/jpeg', 37235);
INSERT INTO tbl_file_metadata VALUES (45, NOW(), 'profile_45.jpg', 'image/jpeg', 34474);
INSERT INTO tbl_file_metadata VALUES (46, NOW(), 'profile_46.jpg', 'image/jpeg', 35299);
INSERT INTO tbl_file_metadata VALUES (47, NOW(), 'profile_47.jpg', 'image/jpeg', 21321);
INSERT INTO tbl_file_metadata VALUES (48, NOW(), 'profile_48.jpg', 'image/jpeg', 17798);
INSERT INTO tbl_file_metadata VALUES (49, NOW(), 'profile_49.jpg', 'image/jpeg', 38518);
INSERT INTO tbl_file_metadata VALUES (50, NOW(), 'profile_50.jpg', 'image/jpeg', 37910);

-- tbl_employees
INSERT INTO tbl_employees VALUES (1, 1, 3, '직원1', 'user1@example.com', 'EMP-20210912-001', '차장', '2021-09-12', 'RESIGNED', NOW(), NOW());
INSERT INTO tbl_employees VALUES (2, 2, 4, '직원2', 'user2@example.com', 'EMP-20230202-002', '부장', '2023-02-02', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (3, 3, 5, '직원3', 'user3@example.com', 'EMP-20221205-003', '사원', '2022-12-05', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (4, 4, 4, '직원4', 'user4@example.com', 'EMP-20220305-004', '대리', '2022-03-05', 'RESIGNED', NOW(), NOW());
INSERT INTO tbl_employees VALUES (5, 5, 4, '직원5', 'user5@example.com', 'EMP-20210720-005', '대리', '2021-07-20', 'ON_LEAVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (6, 6, 1, '직원6', 'user6@example.com', 'EMP-20230706-006', '대리', '2023-07-06', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (7, 7, 1, '직원7', 'user7@example.com', 'EMP-20230418-007', '부장', '2023-04-18', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (8, 8, 3, '직원8', 'user8@example.com', 'EMP-20210908-008', '사원', '2021-09-08', 'ON_LEAVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (9, 9, 4, '직원9', 'user9@example.com', 'EMP-20211217-009', '대리', '2021-12-17', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (10, 10, 2, '직원10', 'user10@example.com', 'EMP-20230201-010', '과장', '2023-02-01', 'RESIGNED', NOW(), NOW());
INSERT INTO tbl_employees VALUES (11, 11, 5, '직원11', 'user11@example.com', 'EMP-20221218-011', '사원', '2022-12-18', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (12, 12, 3, '직원12', 'user12@example.com', 'EMP-20230814-012', '대리', '2023-08-14', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (13, 13, 4, '직원13', 'user13@example.com', 'EMP-20210423-013', '차장', '2021-04-23', 'RESIGNED', NOW(), NOW());
INSERT INTO tbl_employees VALUES (14, 14, 2, '직원14', 'user14@example.com', 'EMP-20231022-014', '대리', '2023-10-22', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (15, 15, 4, '직원15', 'user15@example.com', 'EMP-20230826-015', '사원', '2023-08-26', 'RESIGNED', NOW(), NOW());
INSERT INTO tbl_employees VALUES (16, 16, 3, '직원16', 'user16@example.com', 'EMP-20230215-016', '대리', '2023-02-15', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (17, 17, 1, '직원17', 'user17@example.com', 'EMP-20230305-017', '차장', '2023-03-05', 'ON_LEAVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (18, 18, 5, '직원18', 'user18@example.com', 'EMP-20200228-018', '부장', '2020-02-28', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (19, 19, 5, '직원19', 'user19@example.com', 'EMP-20200807-019', '대리', '2020-08-07', 'ON_LEAVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (20, 20, 2, '직원20', 'user20@example.com', 'EMP-20211117-020', '사원', '2021-11-17', 'ON_LEAVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (21, 21, 1, '직원21', 'user21@example.com', 'EMP-20200114-021', '사원', '2020-01-14', 'ON_LEAVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (22, 22, 5, '직원22', 'user22@example.com', 'EMP-20200222-022', '사원', '2020-02-22', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (23, 23, 5, '직원23', 'user23@example.com', 'EMP-20231125-023', '대리', '2023-11-25', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (24, 24, 4, '직원24', 'user24@example.com', 'EMP-20221016-024', '부장', '2022-10-16', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (25, 25, 4, '직원25', 'user25@example.com', 'EMP-20211007-025', '과장', '2021-10-07', 'ON_LEAVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (26, 26, 5, '직원26', 'user26@example.com', 'EMP-20220531-026', '대리', '2022-05-31', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (27, 27, 5, '직원27', 'user27@example.com', 'EMP-20220714-027', '부장', '2022-07-14', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (28, 28, 1, '직원28', 'user28@example.com', 'EMP-20230819-028', '사원', '2023-08-19', 'RESIGNED', NOW(), NOW());
INSERT INTO tbl_employees VALUES (29, 29, 3, '직원29', 'user29@example.com', 'EMP-20230423-029', '사원', '2023-04-23', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (30, 30, 5, '직원30', 'user30@example.com', 'EMP-20221128-030', '부장', '2022-11-28', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (31, 31, 4, '직원31', 'user31@example.com', 'EMP-20230901-031', '대리', '2023-09-01', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (32, 32, 5, '직원32', 'user32@example.com', 'EMP-20231114-032', '차장', '2023-11-14', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (33, 33, 5, '직원33', 'user33@example.com', 'EMP-20230927-033', '대리', '2023-09-27', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (34, 34, 1, '직원34', 'user34@example.com', 'EMP-20211228-034', '사원', '2021-12-28', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (35, 35, 2, '직원35', 'user35@example.com', 'EMP-20210606-035', '차장', '2021-06-06', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (36, 36, 5, '직원36', 'user36@example.com', 'EMP-20210827-036', '부장', '2021-08-27', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (37, 37, 1, '직원37', 'user37@example.com', 'EMP-20230121-037', '부장', '2023-01-21', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (38, 38, 5, '직원38', 'user38@example.com', 'EMP-20221203-038', '과장', '2022-12-03', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (39, 39, 3, '직원39', 'user39@example.com', 'EMP-20220520-039', '과장', '2022-05-20', 'RESIGNED', NOW(), NOW());
INSERT INTO tbl_employees VALUES (40, 40, 3, '직원40', 'user40@example.com', 'EMP-20231217-040', '차장', '2023-12-17', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (41, 41, 2, '직원41', 'user41@example.com', 'EMP-20211129-041', '사원', '2021-11-29', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (42, 42, 4, '직원42', 'user42@example.com', 'EMP-20221020-042', '차장', '2022-10-20', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (43, 43, 4, '직원43', 'user43@example.com', 'EMP-20201221-043', '과장', '2020-12-21', 'RESIGNED', NOW(), NOW());
INSERT INTO tbl_employees VALUES (44, 44, 5, '직원44', 'user44@example.com', 'EMP-20220305-044', '대리', '2022-03-05', 'RESIGNED', NOW(), NOW());
INSERT INTO tbl_employees VALUES (45, 45, 1, '직원45', 'user45@example.com', 'EMP-20200426-045', '차장', '2020-04-26', 'ON_LEAVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (46, 46, 5, '직원46', 'user46@example.com', 'EMP-20210206-046', '차장', '2021-02-06', 'ON_LEAVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (47, 47, 5, '직원47', 'user47@example.com', 'EMP-20220424-047', '대리', '2022-04-24', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (48, 48, 5, '직원48', 'user48@example.com', 'EMP-20210706-048', '차장', '2021-07-06', 'ACTIVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (49, 49, 5, '직원49', 'user49@example.com', 'EMP-20221206-049', '차장', '2022-12-06', 'ON_LEAVE', NOW(), NOW());
INSERT INTO tbl_employees VALUES (50, 50, 1, '직원50', 'user50@example.com', 'EMP-20220216-050', '부장', '2022-02-16', 'ACTIVE', NOW(), NOW());