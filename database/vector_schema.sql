CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE IF NOT EXISTS knowledge_chunk_vector (
  id BIGSERIAL PRIMARY KEY,
  chunk_id VARCHAR(100) NOT NULL,
  content TEXT NOT NULL,
  embedding vector(1536) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_vector_embedding
  ON knowledge_chunk_vector USING ivfflat (embedding vector_cosine_ops);
