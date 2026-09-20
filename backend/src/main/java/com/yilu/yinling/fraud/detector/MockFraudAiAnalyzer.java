package com.yilu.yinling.fraud.detector;

import org.springframework.stereotype.Component;

@Component
public class MockFraudAiAnalyzer implements FraudAiAnalyzer {
    @Override
    public FraudAiResult analyze(String content) {
        String text = content == null ? "" : content;
        if (text.contains("养老补贴") || text.contains("养老金")) return new FraudAiResult("养老补贴诈骗", 0.90);
        if (text.contains("公检法")) return new FraudAiResult("冒充公检法诈骗", 0.90);
        if (text.contains("中奖")) return new FraudAiResult("中奖诈骗", 0.80);
        if (text.contains("转账") || text.contains("验证码") || text.contains("密码")) return new FraudAiResult("金融账户诈骗", 0.75);
        return new FraudAiResult("未发现明显诈骗类型", 0.05);
    }
}
