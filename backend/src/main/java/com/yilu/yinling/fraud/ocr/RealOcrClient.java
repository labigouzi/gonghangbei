package com.yilu.yinling.fraud.ocr;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@ConditionalOnProperty(name = "ocr.provider", havingValue = "aliyun")
public class RealOcrClient implements OcrClient {
    private final AliyunOcrClient aliyun;
    public RealOcrClient(OcrProperties properties) { this.aliyun = new AliyunOcrClient(properties); }
    @Override public OcrResult recognize(MultipartFile file) { return aliyun.recognize(file); }
}
