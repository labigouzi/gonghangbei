package com.yilu.yinling.ai.agent;

import com.yilu.yinling.ai.client.LlmClient;
import com.yilu.yinling.financial.prompt.FinancialPromptTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!demo")
public class DevReportGenerationAgent implements ReportGenerationAgent {
    private final LlmClient llm; private final FinancialPromptTemplate prompt=new FinancialPromptTemplate();
    public DevReportGenerationAgent(LlmClient llm){this.llm=llm;}
    @Override public String generate(AgentContext c){String fallback=prompt.fallback(c.request,c.calculation,c.products);try{String knowledge=c.knowledge.stream().map(x->x.title()+":"+x.chunk()).reduce((a,b)->a+"\n"+b).orElse("暂无");String answer=llm.chat(prompt.build(c.request,c.calculation,c.products)+"\n参考知识：\n"+knowledge,c.message);return answer==null||answer.isBlank()?fallback:answer;}catch(RuntimeException e){return fallback;}}
}
