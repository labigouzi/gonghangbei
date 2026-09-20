package com.yilu.yinling.fraud.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fraud_detection_record")
public class FraudDetectionRecord {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private String inputType;
    private String originalContent;
    private String fileUrl;
    private String ocrContent;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String riskLevel;
    private BigDecimal riskScore;
    private String riskTags;
    private String riskExplanation;
    private String suggestion;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
}
