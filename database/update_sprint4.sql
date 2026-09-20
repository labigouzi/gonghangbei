USE yinling_platform;
CREATE TABLE IF NOT EXISTS fraud_detection_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  input_type VARCHAR(20) NOT NULL,
  original_content TEXT NOT NULL,
  risk_level VARCHAR(20) NOT NULL,
  risk_score DECIMAL(5,2) NOT NULL,
  risk_tags JSON,
  risk_explanation TEXT,
  suggestion TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_fraud_record_user_created(user_id, created_at),
  CONSTRAINT fk_fraud_record_user FOREIGN KEY(user_id) REFERENCES sys_user(id)
);
INSERT INTO fraud_detection_record(user_id, input_type, original_content, risk_level, risk_score, risk_tags, risk_explanation, suggestion)
SELECT id, 'TEXT', '您的养老金账户异常，请点击链接认证', 'HIGH', 85.00, '["养老诈骗","诱导点击"]', '示例：涉及养老金异常和诱导点击链接。', '请停止操作并通过银行官方渠道核实。'
FROM sys_user WHERE username='admin' AND NOT EXISTS (
  SELECT 1 FROM fraud_detection_record f WHERE f.user_id=sys_user.id AND f.original_content='您的养老金账户异常，请点击链接认证'
);
INSERT INTO fraud_detection_record(user_id, input_type, original_content, risk_level, risk_score, risk_tags, risk_explanation, suggestion)
SELECT id, 'TEXT', '银行通知您存款到期，请前往官方渠道办理', 'LOW', 5.00, '[]', '示例：未发现明显诈骗关键词。', '请通过银行官方渠道核实重要通知。'
FROM sys_user WHERE username='admin' AND NOT EXISTS (
  SELECT 1 FROM fraud_detection_record f WHERE f.user_id=sys_user.id AND f.original_content='银行通知您存款到期，请前往官方渠道办理'
);
