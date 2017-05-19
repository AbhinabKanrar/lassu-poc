CREATE SCHEMA IF NOT EXISTS lassu;

CREATE TABLE IF NOT EXISTS lassu.web_x_user_detail_audit(
	audit_id BIGINT PRIMARY KEY,
	user_id BIGINT NOT NULL,
	reporting_site VARCHAR(255) NOT NULL,
	crtn_ts timestamp without time zone NOT NULL,
	CONSTRAINT user_detail1_audit_fk_ FOREIGN KEY (user_id) REFERENCES lassu.web_x_user_detail (user_id)
);
