USE yinling_platform;
ALTER TABLE fraud_detection_record ADD COLUMN IF NOT EXISTS file_name VARCHAR(255) AFTER file_url;
ALTER TABLE fraud_detection_record ADD COLUMN IF NOT EXISTS file_size BIGINT AFTER file_name;
ALTER TABLE fraud_detection_record ADD COLUMN IF NOT EXISTS file_type VARCHAR(100) AFTER file_size;
CREATE INDEX IF NOT EXISTS idx_fraud_record_created_at ON fraud_detection_record(created_at);
