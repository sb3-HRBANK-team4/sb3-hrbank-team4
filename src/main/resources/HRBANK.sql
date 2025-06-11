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
    is_deleted       BOOLEAN     NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ
);

-- 변경 이력 로그 테이블
CREATE TABLE tbl_change_log
(
    id          SERIAL PRIMARY KEY,
    employee_id INT,
    employee_number varchar         NOT NULL,
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
        FOREIGN KEY (employee_id) REFERENCES tbl_employees (id);

ALTER TABLE tbl_backup_history
    ADD CONSTRAINT fk_backup_history_file
        FOREIGN KEY (file_id)
            REFERENCES tbl_file_metadata (id);

ALTER TABLE tbl_change_detail
    ADD CONSTRAINT fk_change_detail_log
        FOREIGN KEY (change_log_id)
            REFERENCES tbl_change_log (id)
            ON DELETE CASCADE;


-- 모든 테이블 데이터 삭제 + 시퀀스 초기화
-- TRUNCATE TABLE tbl_change_detail, tbl_change_log, tbl_backup_history, tbl_employees, tbl_file_metadata, tbl_department RESTART IDENTITY CASCADE;
