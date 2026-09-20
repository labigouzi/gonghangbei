package com.yilu.yinling.ai.client;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "llm")
public class LlmProperties { private boolean enabled; private String provider="mock"; private Provider deepseek=new Provider(); private Provider qwen=new Provider(); @Data public static class Provider { private String apiKey; private String baseUrl; private String model; } }
