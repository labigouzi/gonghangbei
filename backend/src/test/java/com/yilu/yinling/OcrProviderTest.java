package com.yilu.yinling;

import com.yilu.yinling.fraud.ocr.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import static org.junit.jupiter.api.Assertions.*;

class OcrProviderTest {
    @Test void mockProviderReturnsStructuredResult() {
        var result = new MockOcrClient().recognize(new MockMultipartFile("file", "pension.png", "image/png", new byte[]{1}));
        assertEquals("mock", result.provider()); assertTrue(result.content().contains("养老金"));
    }
    @Test void aliyunWithoutKeyFallsBackToMock() {
        var properties = new OcrProperties(); properties.getAliyun().setAccessKeyId(""); properties.getAliyun().setAccessKeySecret("");
        var result = new AliyunOcrClient(properties).recognize(new MockMultipartFile("file", "pension.png", "image/png", new byte[]{1}));
        assertEquals("mock", result.provider());
    }
}
