package com.yilu.yinling.financial.vo;
import com.yilu.yinling.ai.agent.model.AgentModels.*; import java.util.List;
public record FinancialAgentResponse(String answer,String intent,double intentConfidence,List<AgentStep> agentChain,FinancialAnalysis financialAnalysis,List<KnowledgeSource> knowledgeSources){
 public record FinancialAnalysis(FinancialProfile userProfile,RiskResult riskAssessment,List<FinancialPlanResponse.Allocation> allocation,List<FinancialPlanResponse.ProductRecommendation> recommendations,List<String> assumptions){}
}
