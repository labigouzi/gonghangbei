package com.yilu.yinling.ai.service;
import com.yilu.yinling.profile.dto.ProfileRequest;
import org.springframework.stereotype.Service;
@Service
public class MockProfileAnalysisService implements ProfileAnalysisService {
    @Override public ProfileRequest analyze(String description) {
        String text = description == null ? "" : description;
        var ageMatcher = java.util.regex.Pattern.compile("(\\d{2})\\s*岁").matcher(text);
        Integer age = ageMatcher.find() ? Integer.valueOf(ageMatcher.group(1)) : null;
        var incomeMatcher = java.util.regex.Pattern.compile("(\\d{3,6})(?:元|块)?").matcher(text);
        String income = incomeMatcher.find() ? incomeMatcher.group(1) : null;
        String digitalLevel = text.contains("不懂") || text.contains("不会") || text.contains("手机银行") ? "BEGINNER" : "INTERMEDIATE";
        String demand = text.contains("安全") || text.contains("稳健") ? "养老资金安全与稳健规划" : "养老保障与生活规划";
        return new ProfileRequest(age, null, text.contains("退休") ? "已退休" : null, income, demand,
                "LOW", null, digitalLevel, null, null,
                "[\"银龄用户\",\"低风险偏好\",\"数字金融学习需求\"]");
    }
}
