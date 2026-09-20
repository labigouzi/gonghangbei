# Sprint 8 API

## AI 服务状态

`GET /api/v1/ai/status` 需要登录，返回：

```json
{"provider":"mock","enabled":false,"available":false,"knowledgeBase":"CONNECTED"}
```

## 对话可信度

`POST /api/v1/assistant/chat` 的响应新增 `confidence`（0 到 1）、`knowledgeUsed` 和结构化 `sources`。来源字段包含 `title`、`category`、`content`。

可信度由知识命中情况、检索数量、匹配度和回答长度综合计算；无知识命中时为 0.5，知识命中时通常高于无知识回答。

## 真实模型配置

在 `application.yml` 或环境配置中设置：

```yaml
llm:
  enabled: true
  provider: deepseek
  deepseek:
    api-key: ${DEEPSEEK_API_KEY:}
    base-url: ${DEEPSEEK_BASE_URL:https://api.deepseek.com}
    model: ${DEEPSEEK_MODEL:deepseek-chat}
```

通义千问将 provider 改为 `qwen` 并填写对应 Key。缺少 Key、请求异常或返回为空时自动使用 Mock。

本项目不在源码中保存 API Key。Windows PowerShell 启动前设置：

```powershell
$env:DEEPSEEK_API_KEY = "你的新Key"
$env:DEEPSEEK_MODEL = "deepseek-chat"
```

截图中的密钥已经暴露，建议立即在供应商控制台撤销并重新生成。
