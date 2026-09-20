package com.yilu.yinling;

import com.yilu.yinling.fraud.detector.MockFraudAiAnalyzer;
import com.yilu.yinling.fraud.detector.FraudAiAnalyzer;
import com.yilu.yinling.fraud.entity.FraudDetectionRecord;
import com.yilu.yinling.fraud.mapper.FraudDetectionRecordMapper;
import com.yilu.yinling.fraud.rule.RuleEngine;
import com.yilu.yinling.fraud.service.impl.FraudDetectionServiceImpl;
import com.yilu.yinling.fraud.vo.RiskLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

class FraudSprint4Test {
    private final RuleEngine rules = new RuleEngine();

    @Test
    void officialBankNoticeIsLowRisk() {
        var result = rules.evaluate("银行通知您存款到期，请前往官方渠道办理");
        assertEquals(0, result.score());
        assertTrue(result.tags().isEmpty());
    }

    @Test
    void pensionLinkMessageHitsHighRiskSignals() {
        var result = rules.evaluate("您的养老金账户异常，请点击链接认证");
        assertEquals(65, result.score());
        assertTrue(result.tags().contains("养老诈骗"));
        assertTrue(result.tags().contains("诱导点击"));
    }

    @Test
    void mockAiIdentifiesPensionSubsidyScam() {
        var result = new MockFraudAiAnalyzer().analyze("领取国家养老补贴，请点击链接");
        assertEquals("养老补贴诈骗", result.type());
        assertEquals(0.9, result.confidence());
    }

    @Test
    void scoreBandsMatchRiskLevels() {
        assertEquals(RiskLevel.LOW, level(30));
        assertEquals(RiskLevel.MEDIUM, level(31));
        assertEquals(RiskLevel.HIGH, level(61));
        assertEquals(RiskLevel.HIGH, level(85));
        assertEquals(RiskLevel.CRITICAL, level(86));
    }

    @Test
    void detectionPersistsRecordForTheUser() {
        FraudDetectionRecordMapper mapper = mock(FraudDetectionRecordMapper.class);
        FraudAiAnalyzer analyzer = new MockFraudAiAnalyzer();
        var service = new FraudDetectionServiceImpl(mapper, rules, analyzer, new com.fasterxml.jackson.databind.ObjectMapper());
        var result = service.detect(9L, "您的养老金账户异常，请点击链接认证");
        assertEquals(RiskLevel.HIGH, result.riskLevel());
        ArgumentCaptor<FraudDetectionRecord> captor = ArgumentCaptor.forClass(FraudDetectionRecord.class);
        verify(mapper).insert(captor.capture());
        var record = captor.getValue();
        assertEquals(9L, record.getUserId());
        assertEquals("TEXT", record.getInputType());
        assertEquals("HIGH", record.getRiskLevel());
    }

    private RiskLevel level(double score) {
        if (score <= 30) return RiskLevel.LOW;
        if (score <= 60) return RiskLevel.MEDIUM;
        if (score <= 85) return RiskLevel.HIGH;
        return RiskLevel.CRITICAL;
    }
}
