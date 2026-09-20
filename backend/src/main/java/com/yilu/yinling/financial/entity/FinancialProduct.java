package com.yilu.yinling.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("financial_product")
public class FinancialProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String productName;
    private String productType;
    private String riskLevel;
    private Integer minAge;
    private Integer maxAge;
    private String expectedReturn;
    private String liquidity;
    private String description;
    private String suitableTags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
