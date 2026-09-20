USE yinling_platform;
CREATE TABLE IF NOT EXISTS elderly_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL UNIQUE, age INT, gender VARCHAR(20),
  retirement_status VARCHAR(30), monthly_income VARCHAR(50), pension_demand TEXT,
  risk_preference VARCHAR(30), investment_experience VARCHAR(50), digital_finance_level VARCHAR(30),
  health_status VARCHAR(50), family_structure VARCHAR(100), profile_tags JSON,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY(user_id) REFERENCES sys_user(id)
);
INSERT INTO elderly_profile(user_id, age, retirement_status, monthly_income, risk_preference, digital_finance_level, pension_demand, profile_tags)
SELECT id, 65, '已退休', '5000-8000', '稳健', '初级', '养老资金安全与稳健规划', '["银龄用户","稳健型","数字金融学习需求"]' FROM sys_user WHERE username='admin'
ON DUPLICATE KEY UPDATE age=VALUES(age), retirement_status=VALUES(retirement_status), monthly_income=VALUES(monthly_income), risk_preference=VALUES(risk_preference), digital_finance_level=VALUES(digital_finance_level);
