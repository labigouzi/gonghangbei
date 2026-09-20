package com.yilu.yinling.ai.agent.model;

import java.util.List;

public final class AgentModels {
    private AgentModels() { }
    public record IntentResult(String intent, double confidence) { }
    public record RiskResult(int score, String level, List<String> warnings) { }
    public record FinancialProfile(String ageStage, String riskLevel, String financialAbility, List<String> tags) { }
    public record AgentStep(String name, String status, String summary, long durationMs) { }
    public record KnowledgeSource(String title, String chunk, double similarity) { }
}
