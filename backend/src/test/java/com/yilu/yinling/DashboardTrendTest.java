package com.yilu.yinling;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yilu.yinling.admin.service.impl.AdminServiceImpl;
import com.yilu.yinling.ai.entity.Conversation;
import com.yilu.yinling.ai.mapper.ConversationMapper;
import com.yilu.yinling.fraud.entity.FraudDetectionRecord;
import com.yilu.yinling.fraud.mapper.FraudDetectionRecordMapper;
import com.yilu.yinling.profile.mapper.ElderlyProfileMapper;
import com.yilu.yinling.user.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardTrendTest {
    @Test void trendGroupsDailyAndCountsHighRisk() {
        UserMapper users=mock(UserMapper.class); ConversationMapper chats=mock(ConversationMapper.class); FraudDetectionRecordMapper fraud=mock(FraudDetectionRecordMapper.class); ElderlyProfileMapper profiles=mock(ElderlyProfileMapper.class);
        var first = record("LOW", LocalDateTime.of(2026,9,1,10,0)); var second = record("HIGH", LocalDateTime.of(2026,9,1,11,0));
        when(fraud.selectList(any(QueryWrapper.class))).thenReturn(List.of(first, second));
        var service = new AdminServiceImpl(users, chats, fraud, profiles, new ObjectMapper());
        var trend = service.fraudTrend(LocalDate.of(2026,9,1), LocalDate.of(2026,9,2));
        assertEquals(2, trend.size()); assertEquals(2, trend.get(0).total()); assertEquals(1, trend.get(0).highRisk()); assertEquals(0, trend.get(1).total());
    }
    private FraudDetectionRecord record(String level, LocalDateTime time) { FraudDetectionRecord r=new FraudDetectionRecord(); r.setRiskLevel(level); r.setCreatedAt(time); r.setRiskTags("[]"); return r; }
}
