package com.yilu.yinling.ai.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yilu.yinling.ai.entity.Conversation;
import org.springframework.context.annotation.Profile;
@Profile("!test")
public interface ConversationMapper extends BaseMapper<Conversation> {}
