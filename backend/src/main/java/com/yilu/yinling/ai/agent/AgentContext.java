package com.yilu.yinling.ai.agent;

import com.yilu.yinling.ai.agent.model.AgentModels.*;
import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import com.yilu.yinling.financial.service.FinancialPlanCalculator;
import com.yilu.yinling.financial.vo.FinancialPlanResponse;
import java.util.ArrayList;
import java.util.List;

public class AgentContext {
    public final Long userId; public final String message;
    public FinancialPlanRequest request; public IntentResult intent; public FinancialProfile profile; public RiskResult risk;
    public FinancialPlanCalculator.Calculation calculation; public List<FinancialPlanResponse.ProductRecommendation> products=List.of();
    public List<KnowledgeSource> knowledge=List.of(); public String answer; public final List<String> assumptions=new ArrayList<>(); public final List<AgentStep> steps=new ArrayList<>();
    public AgentContext(Long userId,String message){this.userId=userId;this.message=message;}
}
