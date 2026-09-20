package com.yilu.yinling.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("elderly_financial_plan")
public class ElderlyFinancialPlan {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private Integer age;
    private BigDecimal monthlyIncome;
    private BigDecimal assetAmount;
    private String riskPreference;
    private String retirementGoal;
    private String medicalNeed;
    private String travelNeed;
    private String planResult;
    private LocalDateTime createdAt;
}
