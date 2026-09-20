# Sprint 12 比赛展示增强接口

## 认证

以下接口均要求请求头：

```http
Authorization: Bearer <JWT>
```

Demo 账号为 `admin / 123456`。

## 金融健康评分

```http
GET /api/v1/financial/health-score
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "totalScore": 78,
    "dimensions": [
      { "name": "资产安全", "score": 85 },
      { "name": "养老准备", "score": 80 },
      { "name": "风险控制", "score": 78 },
      { "name": "医疗保障", "score": 69 }
    ],
    "riskWarnings": [
      "优先保留日常生活和突发支出的备用资金"
    ],
    "disclaimer": "本评分仅用于养老金融知识展示，不构成投资建议。"
  }
}
```

四个维度均限制在 0-100，总分为四项等权平均。评分用于比赛展示，不是金融产品评级，也不构成投资建议。

## 家庭协同摘要

```http
GET /api/v1/financial/family-summary
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "elderName": "演示管理员",
    "age": 68,
    "retirementStatus": "已退休 · 健康养老规划中",
    "riskReminders": [
      "家人每月共同核对一次养老资金安排",
      "收到转账、验证码或补贴链接时先联系家人核实"
    ]
  }
}
```

## Demo 演示顺序

1. 使用 Demo 账号登录。
2. 在 Dashboard 进入“AI决策中心”，运行六节点 Agent 链。
3. 观察节点从 `processing` 依次变为 `completed`。
4. 返回 Dashboard 进入“金融健康分析”。
5. 展示 78 分总分、四维雷达图、风险提示和家庭协同摘要。

Demo profile 不访问 MySQL、Redis、PostgreSQL、MinIO 或 DeepSeek。
