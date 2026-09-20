package com.yilu.yinling.fraud.ocr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ocr")
public class OcrProperties {
    private boolean enabled;
    private String provider = "mock";
    private Aliyun aliyun = new Aliyun();
    @Data public static class Aliyun { private String endpoint; private String accessKeyId; private String accessKeySecret; }
}
