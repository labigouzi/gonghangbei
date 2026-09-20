package com.yilu.yinling.fraud.ocr;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "ocr.provider", havingValue = "mock", matchIfMissing = true)
public class MockOcrClient implements OcrClient {
    @Override
    public OcrResult recognize(MultipartFile file) {
        String name = file == null || file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        if (name.contains("safe") || name.contains("official")) return new OcrResult("银行通知您存款到期，请前往官方渠道办理", "mock", 0.99);
        return new OcrResult("您的养老金账户异常，请点击链接认证", "mock", 0.99);
    }
}
