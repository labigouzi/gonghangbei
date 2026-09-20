package com.yilu.yinling.ai.rag;
import lombok.Data; import org.springframework.boot.context.properties.ConfigurationProperties;
@Data @ConfigurationProperties(prefix="vector") public class VectorProperties {
    private String provider = "mock";
    private Postgres postgres = new Postgres();
    @Data public static class Postgres { private String url; private String username; private String password; }
}
