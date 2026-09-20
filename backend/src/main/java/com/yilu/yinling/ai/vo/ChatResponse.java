package com.yilu.yinling.ai.vo;
import java.util.List;
public record ChatResponse(String answer, Long conversationId, String safetyNotice, List<String> source,
                           boolean userProfileUsed, boolean personalized, List<Source> sources,
                           double confidence, boolean knowledgeUsed, String reply, String provider,
                           String model, Long latency) {
    public record Source(String title, String category, String content) {}
    public ChatResponse(String answer, Long conversationId, String safetyNotice, List<String> source) {
        this(answer, conversationId, safetyNotice, source, false, false, toSources(source), 0.5, source != null && !source.isEmpty(), answer, "mock", "mock", 0L);
    }
    public ChatResponse(String answer, Long conversationId, String safetyNotice, List<String> source, boolean used, boolean personalized) {
        this(answer, conversationId, safetyNotice, source, used, personalized, toSources(source), 0.5, source != null && !source.isEmpty(), answer, "mock", "mock", 0L);
    }
    public ChatResponse(String answer, Long conversationId, String safetyNotice, List<String> source, boolean used, boolean personalized, List<Source> sources) { this(answer, conversationId, safetyNotice, source, used, personalized, sources, 0.5, source != null && !source.isEmpty(), answer, "mock", "mock", 0L); }
    public ChatResponse(String answer, Long conversationId, String safetyNotice, List<String> source, boolean used, boolean personalized, List<Source> sources, double confidence, boolean knowledgeUsed) { this(answer, conversationId, safetyNotice, source, used, personalized, sources, confidence, knowledgeUsed, answer, "deepseek", "deepseek-chat", 0L); }
    private static List<Source> toSources(List<String> source) { return source == null ? List.of() : source.stream().map(s -> new Source("养老金融知识", "养老金融", s)).toList(); }
}
