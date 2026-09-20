package com.yilu.yinling.storage;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties { private boolean enabled; private String endpoint; private String publicUrl; private String accessKey; private String secretKey; private String bucket = "yinling-files"; }
