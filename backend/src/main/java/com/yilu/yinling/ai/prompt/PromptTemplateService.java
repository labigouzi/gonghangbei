package com.yilu.yinling.ai.prompt;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.yilu.yinling.profile.vo.ProfileView;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PromptTemplateService {
    public String elderlyAssistantPrompt() {
        try (var input = new ClassPathResource("prompts/elderly-assistant.txt").getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "你是一名面向老年用户的金融陪伴助手。请使用简单易懂的语言，不提供投资买卖建议，不承诺收益。";
        }
    }
    public String fraudAnalysisPrompt() { return "预留：用于金融诈骗风险分析，不在本 Sprint 实现。"; }
    public String profileAnalysisPrompt() { return "预留：用于银龄用户画像分析，不在本 Sprint 实现。"; }
    public String personalizedAssistantPrompt(ProfileView profile) {
        if (profile == null) return elderlyAssistantPrompt();
        return elderlyAssistantPrompt() + "\n\n用户画像（仅用于个性化表达，不用于交易决策）：\n"
                + "年龄：" + value(profile.age()) + "\n"
                + "收入：" + value(profile.monthlyIncome()) + "\n"
                + "风险偏好：" + value(profile.riskPreference()) + "\n"
                + "数字金融能力：" + value(profile.digitalFinanceLevel()) + "\n"
                + "养老需求：" + value(profile.pensionDemand()) + "\n"
                + "规则：高龄或金融经验较低时使用短句并解释术语；低风险偏好时强调风险控制；数字金融能力初级时避免复杂操作，涉及资金操作提醒通过官方渠道核实。";
    }
    private String value(Object value) { return value == null || value.toString().isBlank() ? "未填写" : value.toString(); }
}
