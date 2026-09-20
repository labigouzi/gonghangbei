# Sprint 12 比赛展示增强设计

## 目标与边界

Sprint 12 仅增强比赛展示，不增加复杂业务，不改变 Sprint 10 养老规划、Sprint 11 Agent 编排、DeepSeek 接入或诈骗检测链路。dev 继续使用现有画像和规划能力；demo 全程使用内存演示数据，不访问 MySQL、Redis、PostgreSQL、MinIO 或 LLM。

## 总体架构

```text
Dashboard
  |-- AI决策中心 /agent
  |     `-- Agent执行动画（processing -> completed）
  |
  `-- 金融健康分析 /health-score
        |-- GET /api/v1/financial/health-score
        |     `-- FinancialHealthService -> FinancialHealthScoreCalculator
        `-- GET /api/v1/financial/family-summary
              `-- FamilySummaryService
```

健康评分和家庭摘要是独立的只读展示能力。它们不写数据库，不调用 LLM，不构成投资建议。

## 后端设计

新增 `financial.health` 包：

- `FinancialHealthScoreCalculator`：按输入计算四项维度与总分。
- `FinancialHealthService`：根据当前用户组装评分数据。
- `FamilySummaryService`：返回家庭协同摘要。
- `FinancialHealthController`：暴露两个 JWT 保护接口。
- `FinancialHealthScore`：总分、维度、风险提示和免责声明。
- `FamilySummary`：老人姓名、年龄、养老状态、风险提醒。

接口：

```http
GET /api/v1/financial/health-score
GET /api/v1/financial/family-summary
Authorization: Bearer <JWT>
```

评分维度均为 0-100：

- 资产安全：依据资产规模、生活备用金覆盖能力。
- 养老准备：依据退休收入与养老目标完整度。
- 风险控制：依据风险偏好和风险提示意识。
- 医疗保障：依据医疗需求是否已被识别和预留。

总分为四项等权平均并限制在 0-100。评分结果必须携带“仅用于养老金融知识展示，不构成投资建议”的免责声明。

dev 通过可选依赖读取现有 `ProfileService` 和最近养老规划；数据缺失时使用明确的展示默认值。demo 使用固定数据，确保无外部依赖并稳定返回 78 分附近的演示结果。

## 前端设计

### AI 决策中心

现有 `/agent` 页面保留六个节点。提交后节点先进入 `processing`，再随请求完成依次进入 `completed`；请求失败时停止动画并恢复可操作状态。动画仅表达执行状态，不伪造后端中间结果。

### 金融健康分析

新增 `/health-score` 页面：

- 总分仪表盘；
- 四维雷达图；
- 风险提示列表；
- 家庭协同区域，展示老人姓名、年龄、养老状态和风险提醒；
- 固定免责声明。

Dashboard 增加“AI决策中心”和“金融健康分析”入口。“AI决策中心”指向现有 `/agent`。

## 错误与安全

- 两个新接口必须经过 JWT 认证，未登录返回 401。
- 数据缺失返回可解释的展示默认值，不抛出空指针。
- 分数统一限制在 0-100。
- 前端接口失败时显示清晰提示，不展示过期结果。
- 不输出敏感画像或凭据到日志。

## 测试与验收

新增 `FinancialHealthTest`：

1. 验证四维评分和总分边界；
2. 验证 demo 健康评分与家庭摘要；
3. 验证登录后可访问；
4. 验证未登录返回 401。

最终执行：

```text
mvn test -q
npm run build
```

并启动 demo JAR，实际验证登录、两个新接口、健康状态和既有 Agent 接口。
