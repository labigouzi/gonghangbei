package com.yilu.yinling.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_conversation")
public class Conversation {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private String title; private String status;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
}
