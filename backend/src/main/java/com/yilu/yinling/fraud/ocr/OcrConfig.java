package com.yilu.yinling.fraud.ocr;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OcrProperties.class)
public class OcrConfig {
    @Bean
    @ConditionalOnMissingBean(OcrClient.class)
    public OcrClient fallbackOcrClient() { return new MockOcrClient(); }
}
