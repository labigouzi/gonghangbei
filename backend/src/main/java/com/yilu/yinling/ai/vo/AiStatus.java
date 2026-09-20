package com.yilu.yinling.ai.vo;
public record AiStatus(String provider, String model, boolean enabled, boolean available, boolean fallback, String message, String knowledgeBase) {}
