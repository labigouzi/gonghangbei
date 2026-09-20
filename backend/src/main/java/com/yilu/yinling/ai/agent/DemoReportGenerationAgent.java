package com.yilu.yinling.ai.agent;
import com.yilu.yinling.financial.prompt.FinancialPromptTemplate; import org.springframework.context.annotation.Profile; import org.springframework.stereotype.Component;
@Component @Profile("demo") public class DemoReportGenerationAgent implements ReportGenerationAgent { private final FinancialPromptTemplate prompt=new FinancialPromptTemplate(); public String generate(AgentContext c){return prompt.fallback(c.request,c.calculation,c.products);} }
