package com.yilu.yinling;

import com.yilu.yinling.storage.*;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.beans.factory.ObjectProvider;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class StorageTest {
    @Test void disabledStorageReturnsStablePublicUrl() {
        StorageProperties properties = new StorageProperties(); properties.setEnabled(false); properties.setPublicUrl("http://localhost/files");
        ObjectProvider<MinioClient> provider = mock(ObjectProvider.class);
        String url = new MinioStorageService(properties, provider).upload(new MockMultipartFile("file", "notice.png", "image/png", new byte[]{1,2}));
        assertTrue(url.startsWith("http://localhost/files/")); assertTrue(url.endsWith("-notice.png"));
    }
}
