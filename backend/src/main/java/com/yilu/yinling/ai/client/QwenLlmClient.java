package com.yilu.yinling.ai.client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
@Component
@org.springframework.context.annotation.Primary
@ConditionalOnProperty(name="llm.provider", havingValue="qwen")
public class QwenLlmClient implements LlmClient {
    private static final Logger log=LoggerFactory.getLogger(QwenLlmClient.class);
    private final LlmProperties p; private final MockLlmClient fallback; private final LlmRuntimeState state;
    public QwenLlmClient(LlmProperties p,MockLlmClient fallback,LlmRuntimeState state){this.p=p;this.fallback=fallback;this.state=state;}
    @Override public String chat(String systemPrompt,String userMessage){if(!p.isEnabled()||p.getQwen().getApiKey()==null||p.getQwen().getApiKey().isBlank()){state.fallback(System.currentTimeMillis(),System.currentTimeMillis(),"missing-key");log.info("LLM provider=qwen model={} status=FALLBACK",p.getQwen().getModel());return fallback.chat(systemPrompt,userMessage);}return new DeepSeekLlmClient(p,fallback,state).call(p.getQwen(),systemPrompt,userMessage,"qwen");}
}
