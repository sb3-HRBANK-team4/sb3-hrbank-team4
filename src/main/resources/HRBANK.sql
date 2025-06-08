CREATE SCHEMA IF NOT EXISTS hrbank;

SET search_path TO hrbank, public;

CREATE TABLE tbl_department (
                                id               SERIAL PRIMARY KEY,
                                name             VARCHAR     NOT NULL,
                                description      VARCHAR     NOT NULL,
                                established_date DATE        NOT NULL,
                                created_at       TIMESTAMPTZ NOT NULL,
                                updated_at       TIMESTAMPTZ
);

CREATE TABLE tbl_file_metadata (
                                   id           SERIAL PRIMARY KEY,
                                   created_at   TIMESTAMPTZ NOT NULL,
                                   file_name    VARCHAR     NOT NULL,
                                   content_type VARCHAR     NOT NULL,
                                   size         BIGINT      NOT NULL
);

CREATE TABLE tbl_employees (
                               id               SERIAL PRIMARY KEY,
                               profile_image_id INT,
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

CREATE TABLE tbl_change_log (
                                id          SERIAL PRIMARY KEY,
                                employee_id INT         NOT NULL,
                                changed_at  TIMESTAMPTZ NOT NULL,
                                changed_ip  VARCHAR(50) NOT NULL,
                                type        VARCHAR     NOT NULL,
                                memo        TEXT,
                                created_at  TIMESTAMPTZ NOT NULL
);

CREATE TABLE tbl_change_detail (
                                   id            SERIAL PRIMARY KEY,
                                   change_log_id INT         NOT NULL,
                                   field_name    VARCHAR     NOT NULL,
                                   old_value     VARCHAR     NOT NULL,
                                   new_value     VARCHAR     NOT NULL,
                                   created_at    TIMESTAMPTZ NOT NULL
);

CREATE TABLE tbl_backup_history (
                                    id         SERIAL PRIMARY KEY,
                                    created_at TIMESTAMPTZ   NOT NULL,
                                    worker     VARCHAR       NOT NULL,
                                    started_at TIMESTAMPTZ   NOT NULL,
                                    ended_at   TIMESTAMPTZ   NOT NULL,
                                    status     VARCHAR       NOT NULL,
                                    file_id    INT
);

ALTER TABLE tbl_employees
    ADD CONSTRAINT fk_employees_profile_image
        FOREIGN KEY (profile_image_id)
            REFERENCES tbl_file_metadata(id)
            ON DELETE SET NULL;

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

ALTER TABLE tbl_employees
    ALTER COLUMN profile_image_id DROP NOT NULL;

ALTER TABLE tbl_employees
    ALTER COLUMN department_id DROP NOT NULL;