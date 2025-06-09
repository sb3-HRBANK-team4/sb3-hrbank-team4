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
    profile_image_id INT,
    department_id    INT         NOT NULL,
    name             VARCHAR     NOT NULL,
    email            VARCHAR     NOT NULL,
    employee_number  VARCHAR     NOT NULL,
    position         VARCHAR     NOT NULL,
    hire_date        DATE        NOT NULL,
    status           VARCHAR     NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL,
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
    old_value     VARCHAR,
    new_value     VARCHAR,
    created_at    TIMESTAMPTZ NOT NULL
);

-- 백업 기록 테이블
CREATE TABLE tbl_backup_history
(
    id         SERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    worker     VARCHAR     NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    ended_at   TIMESTAMPTZ NOT NULL,
    status     VARCHAR     NOT NULL,
    file_id    INT
);

-- FK 제약 조건
ALTER TABLE tbl_employees
    ADD CONSTRAINT fk_employees_profile_image
        FOREIGN KEY (profile_image_id)
            REFERENCES tbl_file_metadata (id)
            on delete set null;

ALTER TABLE tbl_employees
    ADD CONSTRAINT fk_employees_department
        FOREIGN KEY (department_id)
            REFERENCES tbl_department (id);

ALTER TABLE tbl_change_log
    ADD CONSTRAINT fk_change_log_employee
        FOREIGN KEY (employee_id)
            REFERENCES tbl_employees (id);

ALTER TABLE tbl_backup_history
    ADD CONSTRAINT fk_backup_history_file
        FOREIGN KEY (file_id)
            REFERENCES tbl_file_metadata (id);

ALTER TABLE tbl_change_detail
    ADD CONSTRAINT fk_change_detail_log
        FOREIGN KEY (change_log_id)
            REFERENCES tbl_change_log (id)
            ON DELETE CASCADE;

insert into tbl_department
values (1, '개발팀', '백엔드', '2025-01-01', now(), now());
insert into tbl_department
values (2, '개발팀1', '백엔드1', '2025-12-2', now(), now());

insert into tbl_file_metadata
values (1, now(), 'profile.jpg', 'jpg', 1000);

insert into tbl_employees
values (1, 1, 1, 'test', 'test@na.com', 'EMP-111-111', '사원', '2025-06-05', 'ACTIVE', now(), now());

insert into tbl_change_log
values (1, '1', now(), '111.222.333.444', 'UPDATED', '직급 변경', now());
insert into tbl_change_detail
values (1, 1, '개발팀', '사원', '부장', now());

-- 모든 테이블 데이터 삭제 + 시퀀스 초기화
TRUNCATE TABLE tbl_change_detail, tbl_change_log, tbl_backup_history, tbl_employees, tbl_file_metadata, tbl_department RESTART IDENTITY CASCADE;

-- 1. 기존 외래키 제약조건 삭제
ALTER TABLE tbl_change_log
    DROP CONSTRAINT fk_change_log_employee;

-- 2. employee_id를 nullable로 변경
ALTER TABLE tbl_change_log
    ALTER COLUMN employee_id DROP NOT NULL;

-- 3. SET NULL 옵션으로 외래키 재생성
ALTER TABLE tbl_change_log
    ADD CONSTRAINT fk_change_log_employee
        FOREIGN KEY (employee_id) REFERENCES tbl_employees (id) ON DELETE SET NULL;