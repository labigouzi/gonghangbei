USE yinling_platform;
CREATE TABLE IF NOT EXISTS ai_conversation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL, title VARCHAR(200) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_conversation_user(user_id), FOREIGN KEY(user_id) REFERENCES sys_user(id)
);
CREATE TABLE IF NOT EXISTS ai_message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, conversation_id BIGINT NOT NULL, role VARCHAR(20) NOT NULL,
  content TEXT NOT NULL, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_message_conversation(conversation_id), FOREIGN KEY(conversation_id) REFERENCES ai_conversation(id)
);
