package com.yilu.yinling.ai.client;
import java.time.Instant;
public record LlmCallMetrics(String provider, String model, boolean success, boolean fallback, long latency, Instant timestamp, int answerLength) {}
