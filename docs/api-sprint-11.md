# Sprint 11 AI银龄金融智能体

## Agent架构

```text
用户问题
  │
  ▼
IntentRecognitionAgent ── 识别养老规划/金融咨询/风险咨询/诈骗识别/旅游养老
  │
  ▼
UserProfileAgent ───────── 消息提取 → 用户画像 → 最近规划 → 明示默认值
  │
  ▼
RiskAssessmentAgent ────── 独立0-100风险评分与风险提示
  │
  ├── KnowledgeRetrievalAgent ── RAG Top 3知识片段
  ▼
FinancialPlanCalculator ── 复用Sprint 10资金规划规则
  │
  ▼
ProductMatchAgent ──────── 单次调用ProductRecommendationService
  │
  ▼
ReportGenerationAgent ──── 单次调用LlmClient，失败时模板降级
  │
  ▼
回答 + Agent Chain + 金融分析 + 知识来源
```

每次请求使用独立`AgentContext`保存中间结果。产品匹配和LLM最多调用一次，不通过一次大模型请求伪装多Agent。

## 接口

### 运行AI金融智能体

- 方法：`POST`
- 地址：`/api/v1/financial/agent/chat`
- 认证：JWT

请求：

```json
{
  "message": "我68岁，有50万存款，每月退休金6000元，退休后如何养老？"
}
```

响应核心结构：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "answer": "【用户情况】...",
    "intent": "RETIREMENT_PLAN",
    "intentConfidence": 0.95,
    "agentChain": [
      {"name": "意图识别Agent", "status": "completed", "summary": "RETIREMENT_PLAN", "durationMs": 0},
      {"name": "用户画像Agent", "status": "completed", "summary": "退休初期 / MEDIUM", "durationMs": 1},
      {"name": "风险评估Agent", "status": "completed", "summary": "28 / LOW", "durationMs": 0},
      {"name": "资金规划Agent", "status": "completed", "summary": "完成三类资金用途安排", "durationMs": 2},
      {"name": "产品匹配Agent", "status": "completed", "summary": "匹配4项模拟产品", "durationMs": 0},
      {"name": "报告生成Agent", "status": "completed", "summary": "生成适老化规划报告", "durationMs": 1}
    ],
    "financialAnalysis": {
      "userProfile": {"ageStage": "退休初期", "riskLevel": "LOW", "financialAbility": "MEDIUM", "tags": ["稳健", "养老"]},
      "riskAssessment": {"score": 28, "level": "LOW", "warnings": ["警惕高收益承诺"]},
      "allocation": [],
      "recommendations": [],
      "assumptions": []
    },
    "knowledgeSources": [
      {"title": "个人养老金制度介绍", "chunk": "个人养老金相关规则", "similarity": 0.84}
    ]
  }
}
```

## 参数补齐

顺序为消息文本、用户画像、最近一次养老规划、演示默认值。使用默认值时会写入`financialAnalysis.assumptions`。

支持示例：

- `68岁`
- `50万存款`
- `每月退休金6000元`
- `稳健/平衡/激进`

## 风险评分

- `0-30`：LOW，低风险。
- `31-60`：MEDIUM，稳健风险。
- `61-100`：HIGH，较高风险。

分数综合年龄、资产、月收入和风险偏好。评分只用于比赛演示和风险教育，不代替金融机构的适当性评估。

## dev与demo

dev读取用户画像、最近规划、数据库产品和RAG，并通过现有`LlmClient`生成报告。没有修改DeepSeek客户端或配置逻辑。

demo不依赖外部服务，固定示例返回：

- 68岁
- 50万元资产
- 6000元退休金
- 28分/LOW
- 六步Agent Chain
- Mock知识来源
- 本地模板报告
