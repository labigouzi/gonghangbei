package com.yilu.yinling.fraud.service;

import com.yilu.yinling.fraud.vo.FraudRecordView;
import com.yilu.yinling.fraud.vo.FraudResult;
import com.yilu.yinling.fraud.vo.FraudImageResult;
import java.util.List;

public interface FraudDetectionService {
    FraudResult detect(Long userId, String text);
    FraudImageResult detectImage(Long userId, String ocrContent, String fileUrl);
    FraudImageResult detectImage(Long userId, String ocrContent, String fileUrl, String fileName, Long fileSize, String fileType);
    List<FraudRecordView> records(Long userId);
}
