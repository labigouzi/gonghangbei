package com.yilu.yinling;

import com.yilu.yinling.fraud.ocr.MockOcrClient;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OcrSprint5Test {
    @Test
    void mockOcrReturnsTextForUploadedImage() {
        var file = new MockMultipartFile("file", "pension.png", "image/png", new byte[]{1, 2});
        assertTrue(new MockOcrClient().recognize(file).content().contains("养老金"));
    }
}
