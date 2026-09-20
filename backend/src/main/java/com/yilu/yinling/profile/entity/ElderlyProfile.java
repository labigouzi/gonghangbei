package com.yilu.yinling.profile.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("elderly_profile")
public class ElderlyProfile {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private Integer age; private String gender;
    private String retirementStatus; private String monthlyIncome; private String pensionDemand;
    private String riskPreference; private String investmentExperience; private String digitalFinanceLevel;
    private String healthStatus; private String familyStructure; private String profileTags;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}
