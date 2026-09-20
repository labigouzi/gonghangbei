package com.yilu.yinling.storage;
import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {
    @Bean
    @ConditionalOnProperty(name = "storage.enabled", havingValue = "true")
    public MinioClient minioClient(StorageProperties p) { return MinioClient.builder().endpoint(p.getEndpoint()).credentials(p.getAccessKey(), p.getSecretKey()).build(); }
}
