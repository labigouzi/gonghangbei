package com.yilu.yinling;

import com.yilu.yinling.ai.prompt.PromptTemplateService;
import com.yilu.yinling.ai.service.MockProfileAnalysisService;
import com.yilu.yinling.profile.vo.ProfileView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileSprint3Test {
    @Test
    void mockAnalysisExtractsAgeIncomeAndDigitalNeed() {
        var result = new MockProfileAnalysisService().analyze("我今年68岁，退休工资4500，不懂手机银行，希望养老资金安全。");
        assertEquals(68, result.age());
        assertEquals("4500", result.monthlyIncome());
        assertEquals("LOW", result.riskPreference());
        assertEquals("BEGINNER", result.digitalFinanceLevel());
        assertTrue(result.profileTags().contains("数字金融学习需求"));
    }

    @Test
    void promptContainsProfileGuidance() {
        var profile = new ProfileView(1L, 2L, 68, null, "已退休", "4500", "资金安全", "LOW", null, "BEGINNER", null, null, "[\"银龄用户\"]");
        var prompt = new PromptTemplateService().personalizedAssistantPrompt(profile);
        assertTrue(prompt.contains("年龄：68"));
        assertTrue(prompt.contains("风险偏好：LOW"));
        assertTrue(prompt.contains("数字金融能力：BEGINNER"));
        assertTrue(prompt.contains("强调风险控制"));
    }
}
