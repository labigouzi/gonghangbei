package com.yilu.yinling.ai.rag;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.util.List;
import java.sql.DriverManager;

@Component
@ConditionalOnProperty(name="vector.provider", havingValue="pgvector")
public class PgVectorStore implements VectorStore {
    private final EmbeddingService embedding;
    private final VectorProperties properties;
    public PgVectorStore(EmbeddingService embedding, VectorProperties properties) { this.embedding = embedding; this.properties = properties; }
    @Override public List<String> search(String query) { return search(query, 5); }
    @Override public List<String> search(String query, int topK) {
        List<Double> vector = embedding.embed(query);
        String literal = vector.toString().replace(" ", "");
        try (var connection = DriverManager.getConnection(properties.getPostgres().getUrl(), properties.getPostgres().getUsername(), properties.getPostgres().getPassword());
             var statement = connection.prepareStatement("SELECT content FROM knowledge_chunk_vector ORDER BY embedding <=> CAST(? AS vector) LIMIT ?")) {
            statement.setString(1, literal); statement.setInt(2, topK); var rs = statement.executeQuery();
            var result = new java.util.ArrayList<String>(); while (rs.next()) result.add(rs.getString("content")); return result;
        } catch (java.sql.SQLException e) { throw new IllegalStateException("无法连接 pgvector", e); }
    }
    @Override public void save(String chunkId, String content, List<Double> vector) {
        try (var connection = DriverManager.getConnection(properties.getPostgres().getUrl(), properties.getPostgres().getUsername(), properties.getPostgres().getPassword());
             var statement = connection.prepareStatement("INSERT INTO knowledge_chunk_vector(chunk_id,content,embedding) VALUES (?,?,CAST(? AS vector))")) {
            statement.setString(1, chunkId); statement.setString(2, content); statement.setString(3, vector.toString().replace(" ", "")); statement.executeUpdate();
        } catch (java.sql.SQLException e) { throw new IllegalStateException("无法写入 pgvector", e); }
    }
}
