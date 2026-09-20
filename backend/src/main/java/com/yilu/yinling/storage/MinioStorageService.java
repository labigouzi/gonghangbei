package com.yilu.yinling.storage;
import io.minio.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
@Service
public class MinioStorageService implements FileStorageService {
    private final StorageProperties properties; private final ObjectProvider<MinioClient> clientProvider;
    public MinioStorageService(StorageProperties p, ObjectProvider<MinioClient> c) { properties=p; clientProvider=c; }
    @Override public String upload(MultipartFile file) {
        String name = UUID.randomUUID() + "-" + (file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_"));
        if (!properties.isEnabled()) return properties.getPublicUrl() + "/" + name;
        try {
            MinioClient client = clientProvider.getIfAvailable();
            if (client == null) return properties.getPublicUrl() + "/" + name;
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucket()).build());
            if (!exists) client.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
            client.putObject(PutObjectArgs.builder().bucket(properties.getBucket()).object(name).stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());
            return properties.getPublicUrl() + "/" + name;
        } catch (Exception e) { return properties.getPublicUrl() + "/" + name; }
    }
}
