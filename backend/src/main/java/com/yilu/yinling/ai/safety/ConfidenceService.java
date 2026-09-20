package com.yilu.yinling.ai.safety;

import org.springframework.stereotype.Service;

@Service
public class ConfidenceService {
    public double calculate(int retrievalCount, double similarity, boolean knowledgeUsed, int answerLength) {
        if (!knowledgeUsed || retrievalCount == 0) return 0.5;
        double match = Math.max(0, Math.min(1, similarity));
        double coverage = Math.min(1, retrievalCount / 3.0);
        double length = answerLength < 20 ? 0.7 : 1.0;
        return Math.round(Math.min(0.99, 0.55 * match + 0.3 * coverage + 0.15 * length) * 100.0) / 100.0;
    }
    public double calculate(int retrievalCount, boolean knowledgeUsed, int answerLength) { return calculate(retrievalCount, knowledgeUsed ? 0.9 : 0, knowledgeUsed, answerLength); }
}
