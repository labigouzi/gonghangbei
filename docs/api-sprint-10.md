# Sprint 10 AI养老金融规划中心 API

## 接口

### 生成养老金融规划

- 方法：`POST`
- 地址：`/api/v1/financial/plan`
- 认证：`Authorization: Bearer <JWT>`
- 说明：返回知识型养老资金规划和模拟产品建议，不构成投资、保险或购买建议。

请求示例：

```json
{
  "age": 68,
  "monthlyIncome": 6000,
  "assetAmount": 500000,
  "riskPreference": "稳健",
  "retirementGoal": "健康养老",
  "medicalNeed": "慢病复诊便利",
  "travelNeed": "每年旅行2次"
}
```

`medicalNeed`和`travelNeed`可省略或为空。后端分别使用“暂无特别医疗需求”和“暂无特别旅行需求”。

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "planId": 1,
    "riskLevel": "LOW",
    "retirementStage": "退休初期",
    "summary": "根据您的年龄、收入、资产和风险偏好，建议先保障生活与养老安全，再安排灵活消费。",
    "allocation": [
      {"category": "生活备用资金", "amount": 100000.00, "percentage": 20, "description": "用于日常生活和突发开支，优先保证随时可用"},
      {"category": "养老保障资金", "amount": 300000.00, "percentage": 60, "description": "用于长期养老与医疗保障，重视安全和稳定"},
      {"category": "灵活消费资金", "amount": 100000.00, "percentage": 20, "description": "用于旅行、兴趣和家庭陪伴等弹性需求"}
    ],
    "recommendations": [
      {
        "productId": 1,
        "productName": "个人养老金",
        "productType": "养老储蓄",
        "recommendationReason": "适合当前年龄和风险偏好",
        "riskNotice": "产品信息仅作比赛知识演示，不构成购买建议，也不承诺收益。"
      }
    ],
    "report": "【用户情况】...",
    "riskNotice": "规划和产品均为知识参考，不构成投资或购买建议，不承诺收益。",
    "financialChain": ["用户画像", "风险评估", "资金规划", "产品匹配", "AI报告"]
  }
}
```

## 规划规则

- 60岁以下：退休准备阶段。
- 60至69岁：退休初期。
- 70岁及以上：养老保障阶段。
- 稳健/低风险：`LOW`；平衡：`MEDIUM`；激进：`HIGH`。
- 默认用途比例：生活备用资金20%、养老保障资金60%、灵活消费资金20%。

这些比例用于比赛演示，不是针对个人的投资指令。

## 数据库

执行：

```sql
SOURCE database/update_sprint10.sql;
```

新增：

- `financial_product`：保存模拟养老金融产品知识，共10条初始化数据。
- `elderly_financial_plan`：保存用户输入快照及生成的规划结果。

产品字段中的收益描述只用于风险教育，均不承诺真实收益。

## AI链路

```text
JWT用户
  -> 输入与可选需求默认化
  -> 养老阶段和风险等级计算
  -> 20/60/20资金用途规划
  -> 按年龄、风险和目标匹配模拟产品
  -> 六段式Prompt
  -> LlmClient生成报告
  -> 保存规划
  -> 返回financialChain
```

报告固定包含：用户情况、养老需求分析、资金规划建议、金融产品建议、风险提醒、温馨提示。LLM异常时返回本地模板报告，规划规则结果仍可用。

## 环境隔离

### dev

- MySQL读取产品并保存规划。
- 使用现有`LlmClient`，支持DeepSeek/Qwen/Mock及既有降级逻辑。
- 不修改RAG、诈骗检测或OCR链路。

### demo

- 不加载financial Mapper。
- 使用内存产品与本地模板报告。
- 不访问MySQL、Redis、PostgreSQL、MinIO或DeepSeek。
- 演示账号：`admin / 123456`。
