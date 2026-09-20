package com.yilu.yinling.ai.agent;

import com.yilu.yinling.ai.agent.model.AgentModels.IntentResult;
import org.springframework.stereotype.Component;

@Component
public class IntentRecognitionAgent {
    public IntentResult recognize(String message) {
        String text = message == null ? "" : message;
        if (containsAny(text, "诈骗", "短信", "验证码", "点击链接", "被骗")) return new IntentResult("FRAUD_DETECTION", 0.97);
        if (containsAny(text, "旅游养老", "旅行养老", "旅居", "旅行")) return new IntentResult("TRAVEL_RETIREMENT", 0.93);
        if (containsAny(text, "风险", "安全吗", "高收益", "亏损")) return new IntentResult("RISK_CONSULTATION", 0.92);
        if (containsAny(text, "退休", "养老规划", "存款", "养老资金", "如何养老")) return new IntentResult("RETIREMENT_PLAN", 0.95);
        return new IntentResult("FINANCIAL_CONSULTATION", 0.78);
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) if (text.contains(keyword)) return true;
        return false;
    }
}
