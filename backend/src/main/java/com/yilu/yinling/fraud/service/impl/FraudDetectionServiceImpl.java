package com.yilu.yinling.fraud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yilu.yinling.fraud.detector.*;
import com.yilu.yinling.fraud.entity.FraudDetectionRecord;
import com.yilu.yinling.fraud.mapper.FraudDetectionRecordMapper;
import com.yilu.yinling.fraud.rule.RuleEngine;
import com.yilu.yinling.fraud.service.FraudDetectionService;
import com.yilu.yinling.fraud.vo.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Service
@org.springframework.context.annotation.Profile("!demo")
public class FraudDetectionServiceImpl implements FraudDetectionService {
    private final FraudDetectionRecordMapper mapper;
    private final RuleEngine ruleEngine;
    private final FraudAiAnalyzer aiAnalyzer;
    private final ObjectMapper objectMapper;

    public FraudDetectionServiceImpl(FraudDetectionRecordMapper mapper, RuleEngine ruleEngine,
                                     FraudAiAnalyzer aiAnalyzer, ObjectMapper objectMapper) {
        this.mapper = mapper; this.ruleEngine = ruleEngine; this.aiAnalyzer = aiAnalyzer; this.objectMapper = objectMapper;
    }

    @Override
    public FraudResult detect(Long userId, String text) {
        return analyzeAndSave(userId, text, "TEXT", null);
    }

    @Override
    public FraudImageResult detectImage(Long userId, String ocrContent, String fileUrl) {
        FraudResult result = analyzeAndSave(userId, ocrContent, "IMAGE", fileUrl, null, null, null);
        return new FraudImageResult(ocrContent, fileUrl, result);
    }

    @Override
    public FraudImageResult detectImage(Long userId, String ocrContent, String fileUrl, String fileName, Long fileSize, String fileType) {
        FraudResult result = analyzeAndSave(userId, ocrContent, "IMAGE", fileUrl, fileName, fileSize, fileType);
        return new FraudImageResult(ocrContent, fileUrl, result);
    }

    private FraudResult analyzeAndSave(Long userId, String text, String inputType, String fileUrl) { return analyzeAndSave(userId, text, inputType, fileUrl, null, null, null); }
    private FraudResult analyzeAndSave(Long userId, String text, String inputType, String fileUrl, String fileName, Long fileSize, String fileType) {
        var rules = ruleEngine.evaluate(text);
        var ai = aiAnalyzer.analyze(text);
        double score = Math.min(100, rules.score() * 0.6 + ai.confidence() * 100 * 0.4);
        BigDecimal finalScore = BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
        RiskLevel level = level(score);
        String explanation = buildExplanation(rules, ai, level);
        String suggestion = suggestion(level);
        FraudResult result = new FraudResult(level, finalScore, rules.tags(), explanation, suggestion);
        FraudDetectionRecord record = new FraudDetectionRecord();
        record.setUserId(userId); record.setInputType(inputType); record.setOriginalContent(text); record.setFileUrl(fileUrl); record.setOcrContent("IMAGE".equals(inputType) ? text : null); record.setFileName(fileName); record.setFileSize(fileSize); record.setFileType(fileType);
        record.setRiskLevel(level.name()); record.setRiskScore(finalScore); record.setRiskTags(write(rules.tags()));
        record.setRiskExplanation(explanation); record.setSuggestion(suggestion); mapper.insert(record);
        return result;
    }

    @Override
    public List<FraudRecordView> records(Long userId) {
        return mapper.selectList(new QueryWrapper<FraudDetectionRecord>().eq("user_id", userId).orderByDesc("created_at"))
                .stream().map(this::view).toList();
    }

    private RiskLevel level(double score) {
        if (score <= 30) return RiskLevel.LOW;
        if (score <= 60) return RiskLevel.MEDIUM;
        if (score <= 85) return RiskLevel.HIGH;
        return RiskLevel.CRITICAL;
    }
    private String buildExplanation(RuleResult rules, FraudAiResult ai, RiskLevel level) {
        String reasons = rules.reasons().isEmpty() ? "暂未命中明显风险关键词" : String.join("；", rules.reasons());
        return "风险等级为" + level + "。" + reasons + "。AI辅助判断为“" + ai.type() + "”，置信度约" + Math.round(ai.confidence() * 100) + "%。";
    }
    private String suggestion(RiskLevel level) {
        return switch (level) {
            case LOW -> "目前未发现明显风险，仍请通过银行官方渠道核实重要通知。";
            case MEDIUM -> "请先停止操作，不点击陌生链接，不提供验证码，并通过官方客服电话或网点核实。";
            case HIGH, CRITICAL -> "请立即停止转账或信息提供，不点击链接；保留证据并通过银行官方渠道或96110咨询核实。";
        };
    }
    private String write(List<String> tags) { try { return objectMapper.writeValueAsString(tags); } catch (JsonProcessingException e) { return "[]"; } }
    private FraudRecordView view(FraudDetectionRecord r) {
        return new FraudRecordView(r.getId(), r.getInputType(), r.getOriginalContent(), r.getFileUrl(), r.getOcrContent(), r.getFileName(), r.getFileSize(), r.getFileType(), RiskLevel.valueOf(r.getRiskLevel()), r.getRiskScore(), read(r.getRiskTags()), r.getRiskExplanation(), r.getSuggestion(), r.getCreatedAt());
    }
    private List<String> read(String json) { try { return json == null ? List.of() : Arrays.asList(objectMapper.readValue(json, String[].class)); } catch (Exception e) { return List.of(); } }
}
