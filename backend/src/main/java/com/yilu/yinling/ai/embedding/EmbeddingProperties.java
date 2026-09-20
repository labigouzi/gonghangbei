package com.yilu.yinling.ai.embedding;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data @ConfigurationProperties(prefix="embedding")
public class EmbeddingProperties { private String provider="mock"; private int dimension=1536; private String apiKey; private String baseUrl; private String model="text-embedding-3-small"; }
