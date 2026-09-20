# Sprint 7 API

## AI 对话

`POST /api/v1/assistant/chat` 需要 JWT。请求体为 `{ "question": "老人如何做好养老规划？", "conversationId": 1 }`。返回 `answer`、`safetyNotice`、兼容字段 `source`，以及结构化 `sources`（标题和知识片段）。

## 知识库上传

`POST /api/v1/admin/knowledge/upload` 需要 ADMIN 角色，使用 `multipart/form-data` 的 `file` 参数，支持 PDF、TXT、Markdown。服务会保存 MinIO URL、解析文本、按约 800 字符切片、生成 embedding 并写入向量存储，返回 `documentId`、`chunkCount`、`status`。

## AI 配置

`llm.provider` 支持 `mock`、`deepseek`、`qwen`；缺少 API Key 或调用失败时自动回退 Mock。`embedding.provider` 支持 `mock`、`openai`、`qwen`，同样自动回退。默认配置不访问外部服务。

## 向量存储

`vector.provider=mock` 使用内存示例知识；`vector.provider=pgvector` 使用 PostgreSQL `knowledge_chunk_vector` 表的余弦距离检索。初始化脚本为 `database/vector_schema.sql`，Docker Compose 提供 `pgvector/pgvector:pg16` 服务。
