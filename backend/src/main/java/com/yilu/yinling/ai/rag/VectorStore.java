package com.yilu.yinling.ai.rag;

import java.util.List;

public interface VectorStore { List<String> search(String query); default List<String> search(String query, int topK) { return search(query).stream().limit(topK).toList(); } default void save(String chunkId, String content, List<Double> embedding) { } }
