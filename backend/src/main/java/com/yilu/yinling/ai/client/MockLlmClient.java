package com.yilu.yinling.ai.client;

import org.springframework.stereotype.Component;

@Component
public class MockLlmClient implements LlmClient {
    @Override
    public String chat(String systemPrompt, String userMessage) {
        return "根据养老金融知识，建议您从养老金保障、风险管理和资金规划三个方面逐步安排。" +
                "先了解个人养老金和基本养老保障，再结合家庭收支选择稳健、透明的方式。" +
                "本回答仅用于金融知识普及，不构成投资或购买建议。";
    }
}
