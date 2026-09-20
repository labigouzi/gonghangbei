# Sprint 3 用户画像与个性化助手接口

## 1. 获取当前用户画像

`GET /api/v1/profile/me`

需要 `Authorization: Bearer <JWT>`。无画像时 `data` 为 `null`。

## 2. 更新当前用户画像

`PUT /api/v1/profile/me`

请求示例：

```json
{
  "age": 65,
  "retirementStatus": "已退休",
  "monthlyIncome": "5000-8000",
  "pensionDemand": "养老资金安全与稳健规划",
  "riskPreference": "LOW",
  "digitalFinanceLevel": "BEGINNER"
}
```

接口采用按用户唯一画像的 upsert 语义，返回画像视图。

## 3. AI生成画像

`POST /api/v1/profile/generate`

请求：

```json
{"description":"我今年68岁，退休工资4500，不懂手机银行，希望养老资金安全。"}
```

当前使用 `MockProfileAnalysisService`，会提取年龄、收入并生成低风险、数字金融初级等标签。后续可替换为真实 LLM 分析实现。

## 4. 个性化 AI 对话

`POST /api/v1/assistant/chat`

请求格式与 Sprint 2 相同。助手在检索知识前读取当前用户画像，并将年龄、收入、风险偏好、数字金融能力、养老需求加入系统 Prompt。

响应新增字段：

```json
{
  "answer": "...",
  "conversationId": 1,
  "safetyNotice": "...",
  "source": ["个人养老金基础知识"],
  "userProfileUsed": true,
  "personalized": true
}
```

`userProfileUsed` 和 `personalized` 在当前用户存在画像时为 `true`。画像仅调整表达方式，不用于投资交易决策。
