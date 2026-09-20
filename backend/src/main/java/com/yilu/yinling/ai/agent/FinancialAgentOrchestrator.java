package com.yilu.yinling.ai.agent;

import com.yilu.yinling.ai.agent.model.AgentModels.AgentStep;
import com.yilu.yinling.financial.service.FinancialPlanCalculator;
import com.yilu.yinling.financial.vo.FinancialAgentResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FinancialAgentOrchestrator {
 private final IntentRecognitionAgent intent; private final UserProfileAgent profile; private final RiskAssessmentAgent risk; private final FinancialPlanCalculator planning; private final ProductMatchAgent products; private final KnowledgeRetrievalAgent knowledge; private final ReportGenerationAgent report;
 public FinancialAgentOrchestrator(IntentRecognitionAgent i,UserProfileAgent p,RiskAssessmentAgent r,FinancialPlanCalculator f,ProductMatchAgent m,KnowledgeRetrievalAgent k,ReportGenerationAgent g){intent=i;profile=p;risk=r;planning=f;products=m;knowledge=k;report=g;}
 public FinancialAgentResponse orchestrate(Long userId,String message){var c=new AgentContext(userId,message);
  step(c,"意图识别Agent",()->{c.intent=intent.recognize(message);return c.intent.intent();});
  step(c,"用户画像Agent",()->{profile.enrich(c);return c.profile.ageStage()+" / "+c.profile.financialAbility();});
  step(c,"风险评估Agent",()->{c.risk=risk.assess(c.request);return c.risk.score()+" / "+c.risk.level();});
  step(c,"资金规划Agent",()->{c.calculation=planning.calculate(c.request);knowledge.retrieve(c);return "完成三类资金用途安排";});
  step(c,"产品匹配Agent",()->{products.match(c);return "匹配"+c.products.size()+"项模拟产品";});
  step(c,"报告生成Agent",()->{c.answer=report.generate(c);return "生成适老化规划报告";});
  return new FinancialAgentResponse(c.answer,c.intent.intent(),c.intent.confidence(),List.copyOf(c.steps),new FinancialAgentResponse.FinancialAnalysis(c.profile,c.risk,c.calculation.allocations(),c.products,List.copyOf(c.assumptions)),c.knowledge);
 }
 private void step(AgentContext c,String name,java.util.function.Supplier<String> action){long s=System.nanoTime();try{String summary=action.get();c.steps.add(new AgentStep(name,"completed",summary,(System.nanoTime()-s)/1_000_000));}catch(RuntimeException e){c.steps.add(new AgentStep(name,"failed",e.getMessage(),(System.nanoTime()-s)/1_000_000));throw e;}}
}
