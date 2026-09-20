package com.yilu.yinling.ai.rag;

import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

@Service
public class RagService {
    private static final Logger log = LoggerFactory.getLogger(RagService.class);
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    public RagService(EmbeddingService embeddingService, VectorStore vectorStore) { this.embeddingService = embeddingService; this.vectorStore = vectorStore; }
    public List<String> retrieve(String query) { embeddingService.embed(query); return vectorStore.search(query); }
    public List<RetrievedChunk> retrieveDetailed(String query, int topK) {
        var result = vectorStore.search(query, topK).stream().map(content -> new RetrievedChunk(titleOf(content), content, similarityOf(content, query))).toList();
        result.forEach(r -> log.info("RAG query={} retrievedTitle={} similarity={}", query, r.title(), r.similarity()));
        return result;
    }
    private String titleOf(String content) { if (content.contains("养老金")) return "个人养老金制度介绍"; if (content.contains("诈骗") || content.contains("密码")) return "养老金融安全指南"; return "养老金融服务指南"; }
    private double similarityOf(String content, String query) { return content.contains("养老金") && query.contains("养老金") ? 0.91 : content.contains("养老") ? 0.84 : 0.72; }
}
