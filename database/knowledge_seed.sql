USE yinling_platform;
CREATE TABLE IF NOT EXISTS knowledge_document (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, title VARCHAR(255) NOT NULL, category VARCHAR(50) NOT NULL,
  content TEXT NOT NULL, enabled TINYINT NOT NULL DEFAULT 1, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO knowledge_document(title,category,content) VALUES
('个人养老金基础知识','养老金融','个人养老金是基本养老保险的重要补充，居民应结合收入和养老需求理性了解。'),
('老年人金融诈骗防范','养老金融','不要向陌生人提供密码、验证码或转账，遇到可疑信息应通过官方渠道核实。'),
('银行存款基础知识','养老金融','办理存款业务应选择正规银行网点或官方应用，仔细阅读产品说明和风险提示。'),
('数字金融使用指南','养老金融','使用手机银行时应设置安全密码，开启设备保护，不点击来源不明的链接。'),
('养老服务相关知识','养老金融','养老规划应综合考虑基本生活、医疗保障、长期照护和家庭支持等需求。');
