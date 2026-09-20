USE yinling_platform;

CREATE TABLE IF NOT EXISTS financial_product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_name VARCHAR(100) NOT NULL,
  product_type VARCHAR(50) NOT NULL,
  risk_level VARCHAR(30) NOT NULL,
  min_age INT,
  max_age INT,
  expected_return VARCHAR(200),
  liquidity VARCHAR(200),
  description TEXT,
  suitable_tags VARCHAR(500),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_financial_product_name (product_name)
);

CREATE TABLE IF NOT EXISTS elderly_financial_plan (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  age INT NOT NULL,
  monthly_income DECIMAL(14,2) NOT NULL,
  asset_amount DECIMAL(16,2) NOT NULL,
  risk_preference VARCHAR(30) NOT NULL,
  retirement_goal VARCHAR(200) NOT NULL,
  medical_need VARCHAR(500),
  travel_need VARCHAR(500),
  plan_result LONGTEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_financial_plan_user_created (user_id, created_at),
  CONSTRAINT fk_financial_plan_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

INSERT INTO financial_product(product_name, product_type, risk_level, min_age, max_age, expected_return, liquidity, description, suitable_tags) VALUES
('个人养老金', '养老储蓄', 'LOW', 40, 80, '不展示或承诺收益，以官方规则为准', '按个人养老金制度规则办理', '用于了解个人养老金制度和长期养老准备的比赛模拟条目。', '养老,稳健,长期'),
('养老储蓄产品', '储蓄', 'LOW', 50, 90, '不展示或承诺收益，以金融机构公示为准', '以产品正式规则为准', '强调本金安全意识和长期养老安排的模拟储蓄信息。', '稳健,储蓄,银龄'),
('养老保险', '保险', 'LOW', 45, 85, '不以收益作为推荐依据', '通常为长期安排，退保规则需咨询机构', '用于了解养老保障和长期现金流概念的模拟保险信息。', '保障,健康养老,长期'),
('稳健型养老理财', '理财', 'MEDIUM_LOW', 50, 80, '净值可能波动，不承诺收益', '以产品开放和赎回规则为准', '用于演示稳健用户如何识别理财风险，不代表真实产品。', '稳健,理财,风险教育'),
('老年医疗保障服务', '保障', 'LOW', 55, 90, '不适用收益描述', '按服务合同执行', '用于了解医疗费用保障与服务范围的比赛模拟条目。', '医疗,保障,银龄'),
('长期护理保障', '保险', 'LOW', 50, 90, '不适用收益描述', '通常为长期保障安排', '用于了解失能护理保障概念的比赛模拟条目。', '护理,保障,长期'),
('银龄旅行备用金', '储蓄', 'LOW', 55, 90, '不展示或承诺收益', '强调随时可用', '用于演示旅行预算应独立于基本生活与医疗资金。', '旅行,流动性,备用'),
('家庭应急储备', '储蓄', 'LOW', 40, 100, '不展示或承诺收益', '强调较高流动性', '用于演示预留生活和医疗应急资金的重要性。', '应急,家庭,流动性'),
('平衡型养老知识组合', '知识服务', 'MEDIUM', 45, 75, '仅作风险教育，不承诺收益', '不同工具规则不同', '用于解释分散安排概念的模拟知识条目，不对应真实产品。', '平衡,知识,规划'),
('数字金融安全陪伴服务', '服务', 'LOW', 55, 100, '不适用收益描述', '按服务规则使用', '用于帮助银龄用户学习官方渠道、密码保护和防诈骗知识。', '数字金融,安全,学习')
ON DUPLICATE KEY UPDATE product_type=VALUES(product_type), risk_level=VALUES(risk_level), description=VALUES(description), suitable_tags=VALUES(suitable_tags);
