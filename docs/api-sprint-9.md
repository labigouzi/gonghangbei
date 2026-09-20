# Sprint 9 验证接口

`GET /api/v1/ai/status` 返回 provider、model、enabled、available、fallback、message 和知识库状态。LLM 未调用或调用失败时 `fallback=true`。

`GET /api/v1/system/health` 返回 MySQL、Redis、向量库和 LLM 状态。默认 Mock 配置显示 `vectorDB=MOCK`；真实 pgvector 配置显示 `CONFIGURED`，真实 LLM 调用成功后显示 `UP`。

RAG 检索内部返回 `RetrievedChunk(title, content, similarity)`，并记录 query、标题和相似度日志。
