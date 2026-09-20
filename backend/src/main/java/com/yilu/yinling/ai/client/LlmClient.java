package com.yilu.yinling.ai.client;

public interface LlmClient {
    String chat(String systemPrompt, String userMessage);
}
