package com.yilu.yinling.fraud.ocr;

import org.springframework.web.multipart.MultipartFile;

public interface OcrClient {
    OcrResult recognize(MultipartFile file);
}
