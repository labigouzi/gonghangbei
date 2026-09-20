USE yinling_platform;
ALTER TABLE fraud_detection_record ADD COLUMN IF NOT EXISTS file_url VARCHAR(500) AFTER original_content;
ALTER TABLE fraud_detection_record ADD COLUMN IF NOT EXISTS ocr_content TEXT AFTER file_url;
