package com.yilu.yinling.ai.embedding;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.util.List;
@Component @ConditionalOnProperty(name="embedding.provider", havingValue="qwen")
public class QwenEmbeddingService implements com.yilu.yinling.ai.rag.EmbeddingService { private final OpenAIEmbeddingService delegate; public QwenEmbeddingService(EmbeddingProperties p){delegate=new OpenAIEmbeddingService(p);} public List<Double> embed(String text){return delegate.embed(text);} }
