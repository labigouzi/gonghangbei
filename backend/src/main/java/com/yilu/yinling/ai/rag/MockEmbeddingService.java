package com.yilu.yinling.ai.rag;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.IntStream;
import com.yilu.yinling.ai.embedding.EmbeddingProperties;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="embedding.provider", havingValue="mock", matchIfMissing=true)
public class MockEmbeddingService implements EmbeddingService {
    private final int dimension;
    public MockEmbeddingService() { this.dimension = 1536; }
    public MockEmbeddingService(EmbeddingProperties properties) { this.dimension = properties.getDimension(); }
    @Override public List<Double> embed(String text) {
        int hash = text == null ? 1 : text.hashCode();
        return IntStream.range(0, dimension).mapToDouble(i -> ((hash * 31L + i) % 1000) / 1000.0).boxed().toList();
    }
}
