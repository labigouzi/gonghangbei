package com.yilu.yinling.ai.embedding;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.*;
@Component @ConditionalOnProperty(name="embedding.provider", havingValue="openai")
public class OpenAIEmbeddingService implements com.yilu.yinling.ai.rag.EmbeddingService {
 private final EmbeddingProperties p; private final com.yilu.yinling.ai.rag.MockEmbeddingService fallback;
 public OpenAIEmbeddingService(EmbeddingProperties p){this.p=p; this.fallback=new com.yilu.yinling.ai.rag.MockEmbeddingService(p);}
 public List<Double> embed(String text){if(p.getApiKey()==null||p.getApiKey().isBlank()||p.getBaseUrl()==null||p.getBaseUrl().isBlank())return fallback.embed(text);try{var body=Map.of("model",p.getModel(),"input",text);var n=RestClient.create(p.getBaseUrl()).post().uri("/embeddings").header(HttpHeaders.AUTHORIZATION,"Bearer "+p.getApiKey()).contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(com.fasterxml.jackson.databind.JsonNode.class);var out=new ArrayList<Double>();n.at("/data/0/embedding").forEach(x->out.add(x.asDouble()));return out.isEmpty()?fallback.embed(text):out;}catch(Exception e){return fallback.embed(text);}}
}
