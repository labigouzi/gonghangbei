# Sprint 2 API

## AI养老金融助手

`POST /api/v1/assistant/chat`

需要携带 Sprint 1 登录得到的请求头：`Authorization: Bearer <token>`。

请求：

```json
{"question":"老人如何做好养老规划？","conversationId":null}
```

响应：

```json
{"code":0,"message":"success","data":{"answer":"...","conversationId":1,"safetyNotice":"...","source":["..."]},"traceId":"..."}
```

调用流程：认证 -> 创建或复用会话 -> Mock Embedding -> Mock VectorStore 检索 -> Prompt 组装 -> Mock LLM -> 安全提示 -> 保存用户与助手消息 -> 返回答案和来源。

当前默认使用 Mock LLM 与 Mock VectorStore，不依赖外部 API Key 或复杂向量数据库；后续可替换实现而不改变业务接口。
