package com.yilu.yinling.ai.client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
@Component
@org.springframework.context.annotation.Primary
@ConditionalOnProperty(name="llm.provider", havingValue="deepseek")
public class DeepSeekLlmClient implements LlmClient {
    private static final Logger log = LoggerFactory.getLogger(DeepSeekLlmClient.class);
    private final LlmProperties p; private final MockLlmClient fallback; private final LlmRuntimeState state;
    public DeepSeekLlmClient(LlmProperties p, MockLlmClient fallback, LlmRuntimeState state){this.p=p;this.fallback=fallback;this.state=state;}
    @PostConstruct
    void init() {
        boolean present = p.getDeepseek() != null && p.getDeepseek().getApiKey() != null && !p.getDeepseek().getApiKey().isBlank();
        log.info("DeepSeek client loaded, apiKeyPresent={}", present);
    }
    public String chat(String systemPrompt,String userMessage){ long start=System.currentTimeMillis(); if(!p.isEnabled()||p.getDeepseek().getApiKey()==null||p.getDeepseek().getApiKey().isBlank()){state.fallback(start,System.currentTimeMillis(),"missing-key"); log.info("LLM provider=deepseek model={} status=FALLBACK",p.getDeepseek().getModel()); return fallback.chat(systemPrompt,userMessage); } return call(p.getDeepseek(),systemPrompt,userMessage); }
    protected String call(LlmProperties.Provider cfg,String system,String user){return call(cfg,system,user,"deepseek");}
    protected String call(LlmProperties.Provider cfg,String system,String user,String provider){ long start=System.currentTimeMillis(); try { String base=cfg.getBaseUrl()==null||cfg.getBaseUrl().isBlank()?"https://api.deepseek.com":cfg.getBaseUrl(); var body=new java.util.LinkedHashMap<String,Object>(); body.put("model",cfg.getModel()==null?"deepseek-chat":cfg.getModel()); body.put("messages",java.util.List.of(java.util.Map.of("role","system","content",system),java.util.Map.of("role","user","content",user))); body.put("temperature",0.3); body.put("stream",false); var node=RestClient.create(base).post().uri("/chat/completions").header(HttpHeaders.AUTHORIZATION,"Bearer "+cfg.getApiKey()).contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(com.fasterxml.jackson.databind.JsonNode.class); String answer=node==null?null:node.at("/choices/0/message/content").asText(null); if(answer==null||answer.isBlank()) throw new IllegalStateException("empty LLM response"); long end=System.currentTimeMillis(); state.success(start,end,provider,cfg.getModel(),answer.length()); log.info("LLM provider={} model={} status=SUCCESS responseTime={}ms", provider,cfg.getModel(),end-start); return answer; } catch(Exception e){ long end=System.currentTimeMillis(); state.fallback(start,end,e.getClass().getSimpleName()); log.warn("LLM provider={} model={} status=FALLBACK responseTime={}ms",provider,cfg.getModel(),end-start); return fallback.chat(system,user); } }
}
