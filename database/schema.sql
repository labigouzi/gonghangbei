CREATE DATABASE IF NOT EXISTS yinling_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE yinling_platform;
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL,
  real_name VARCHAR(50), phone VARCHAR(20), avatar_url VARCHAR(500), status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS sys_role (id BIGINT PRIMARY KEY AUTO_INCREMENT, role_code VARCHAR(50) NOT NULL UNIQUE, role_name VARCHAR(100) NOT NULL);
CREATE TABLE IF NOT EXISTS sys_user_role (user_id BIGINT NOT NULL, role_id BIGINT NOT NULL, PRIMARY KEY(user_id, role_id), FOREIGN KEY(user_id) REFERENCES sys_user(id), FOREIGN KEY(role_id) REFERENCES sys_role(id));
INSERT INTO sys_role(role_code, role_name) VALUES ('USER','普通用户'),('ADMIN','系统管理员') ON DUPLICATE KEY UPDATE role_name=VALUES(role_name);
INSERT INTO sys_user(username,password,real_name,status) VALUES ('admin','$2a$10$WdWW8xEypD0klawAzoBXsuZ1qHb7yu3iwt1gTdTqiQzAuxVSM48y.','系统管理员',1) ON DUPLICATE KEY UPDATE username=username;
INSERT IGNORE INTO sys_user_role(user_id, role_id) SELECT u.id,r.id FROM sys_user u JOIN sys_role r ON r.role_code='ADMIN' WHERE u.username='admin';
