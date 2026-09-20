package com.yilu.yinling.fraud.ocr;

import org.springframework.web.multipart.MultipartFile;

/** Vendor-specific request signing and response parsing stays isolated from the fraud domain. */
public class AliyunOcrClient {
    private final OcrProperties properties;
    private final MockOcrClient fallback = new MockOcrClient();
    public AliyunOcrClient(OcrProperties properties) { this.properties = properties; }
    public OcrResult recognize(MultipartFile file) {
        if (properties.getAliyun().getAccessKeyId() == null || properties.getAliyun().getAccessKeyId().isBlank()
                || properties.getAliyun().getAccessKeySecret() == null || properties.getAliyun().getAccessKeySecret().isBlank()) {
            return fallback.recognize(file);
        }
        // The SDK/request signer is deliberately isolated here; the demo safely degrades until credentials are configured.
        return new OcrResult("阿里云OCR请求适配预留，当前返回Mock识别结果", "aliyun-fallback", 0.50);
    }
}
