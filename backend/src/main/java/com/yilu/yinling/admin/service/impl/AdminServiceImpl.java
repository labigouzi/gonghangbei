package com.yilu.yinling.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yilu.yinling.admin.service.AdminService;
import com.yilu.yinling.admin.vo.*;
import com.yilu.yinling.ai.entity.Conversation;
import com.yilu.yinling.ai.mapper.ConversationMapper;
import com.yilu.yinling.fraud.entity.FraudDetectionRecord;
import com.yilu.yinling.fraud.mapper.FraudDetectionRecordMapper;
import com.yilu.yinling.profile.entity.ElderlyProfile;
import com.yilu.yinling.profile.mapper.ElderlyProfileMapper;
import com.yilu.yinling.user.entity.User;
import com.yilu.yinling.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.*;

@Service
@org.springframework.context.annotation.Profile("!demo")
public class AdminServiceImpl implements AdminService {
    private final UserMapper userMapper; private final ConversationMapper conversationMapper;
    private final FraudDetectionRecordMapper fraudMapper; private final ElderlyProfileMapper profileMapper; private final ObjectMapper objectMapper;
    public AdminServiceImpl(UserMapper u, ConversationMapper c, FraudDetectionRecordMapper f, ElderlyProfileMapper p, ObjectMapper o) { userMapper=u; conversationMapper=c; fraudMapper=f; profileMapper=p; objectMapper=o; }
    @Override public OverviewStats overview() { return overview(null, null); }
    @Override public OverviewStats overview(LocalDate start, LocalDate end) {
        if (start == null && end == null) return new OverviewStats(userMapper.selectCount(new QueryWrapper<User>()), conversationMapper.selectCount(new QueryWrapper<Conversation>()), fraudMapper.selectCount(new QueryWrapper<FraudDetectionRecord>()), highRiskCount(fraudMapper.selectList(new QueryWrapper<>())));
        var bounds = bounds(start, end); var users = userMapper.selectList(new QueryWrapper<User>().ge("created_at", bounds[0]).lt("created_at", bounds[1]));
        var chats = conversationMapper.selectList(new QueryWrapper<Conversation>().ge("created_at", bounds[0]).lt("created_at", bounds[1]));
        var records = fraudMapper.selectList(new QueryWrapper<FraudDetectionRecord>().ge("created_at", bounds[0]).lt("created_at", bounds[1]));
        return new OverviewStats(users.size(), chats.size(), records.size(), highRiskCount(records));
    }
    private long highRiskCount(List<FraudDetectionRecord> records) { return records.stream().filter(r -> "HIGH".equals(r.getRiskLevel()) || "CRITICAL".equals(r.getRiskLevel())).count(); }
    @Override public RiskStatistics fraudStatistics() {
        var records = fraudMapper.selectList(new QueryWrapper<>());
        Map<String, Long> levels = records.stream().collect(Collectors.groupingBy(FraudDetectionRecord::getRiskLevel, Collectors.counting()));
        Map<String, Long> tags = new LinkedHashMap<>();
        records.forEach(r -> { if (r.getRiskTags() != null) { try { objectMapper.readValue(r.getRiskTags(), String[].class); Arrays.stream(objectMapper.readValue(r.getRiskTags(), String[].class)).filter(Objects::nonNull).map(String::trim).filter(s -> !s.isBlank()).forEach(t -> tags.merge(t, 1L, Long::sum)); } catch (Exception ignored) { } } });
        return new RiskStatistics(levels, tags);
    }
    @Override public ProfileStatistics profileStatistics() {
        var profiles = profileMapper.selectList(new QueryWrapper<>());
        return new ProfileStatistics(group(profiles, ElderlyProfile::getRiskPreference), group(profiles, ElderlyProfile::getDigitalFinanceLevel));
    }
    @Override public List<TrendVO> fraudTrend(LocalDate start, LocalDate end) {
        LocalDate from = start == null ? LocalDate.now().minusDays(29) : start; LocalDate to = end == null ? LocalDate.now() : end;
        var records = fraudMapper.selectList(new QueryWrapper<FraudDetectionRecord>().ge("created_at", from.atStartOfDay()).lt("created_at", to.plusDays(1).atStartOfDay()));
        Map<LocalDate, List<FraudDetectionRecord>> grouped = records.stream().filter(r -> r.getCreatedAt() != null).collect(Collectors.groupingBy(r -> r.getCreatedAt().toLocalDate()));
        List<TrendVO> result = new ArrayList<>(); for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) { var day = grouped.getOrDefault(date, List.of()); result.add(new TrendVO(date.toString(), day.size(), day.stream().filter(r -> "HIGH".equals(r.getRiskLevel()) || "CRITICAL".equals(r.getRiskLevel())).count())); } return result;
    }
    private LocalDateTime[] bounds(LocalDate start, LocalDate end) { LocalDate from = start == null ? LocalDate.of(1970,1,1) : start; LocalDate to = end == null ? LocalDate.of(9999,12,30) : end; return new LocalDateTime[]{from.atStartOfDay(), to.plusDays(1).atStartOfDay()}; }
    private <T> Map<String, Long> group(List<ElderlyProfile> list, Function<ElderlyProfile,String> getter) { return list.stream().map(getter).filter(Objects::nonNull).collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting())); }
}
