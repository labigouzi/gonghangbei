# Sprint 12 比赛展示增强实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 增加可动画展示的 Agent 决策链、银龄金融健康评分和家庭协同摘要，同时保持 dev/demo 隔离及既有接口兼容。

**Architecture:** 健康评分作为 `financial.health` 下的只读领域能力，通过计算器和服务分离规则与数据装配。dev 使用现有画像/规划的可选依赖，demo 使用固定内存快照。前端新增独立健康分析页，并仅增强现有 Agent 页的状态表现。

**Tech Stack:** Java 21、Spring Boot 3.3、Spring Security/JWT、JUnit 5、Vue 3、TypeScript、Element Plus、ECharts。

## Global Constraints

- 不改变 Sprint 10、Sprint 11、DeepSeek 和诈骗检测接口。
- demo 不访问 MySQL、Redis、PostgreSQL、MinIO 或 LLM。
- 两个新增接口均要求 JWT。
- 健康评分仅作展示，不构成投资建议。

---

### Task 1: 金融健康评分模型

**Files:**
- Create: `backend/src/test/java/com/yilu/yinling/FinancialHealthTest.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/health/FinancialHealthScoreCalculator.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/health/FinancialHealthScore.java`

**Interfaces:**
- Produces: `FinancialHealthScoreCalculator.calculate(HealthInput)` 返回总分、四项维度、风险提示和免责声明。

- [ ] 写测试，断言 demo 输入产生 78 分、四项维度且所有分数位于 0-100。
- [ ] 运行 `mvn -q -Dtest=FinancialHealthTest test`，确认因类型缺失而失败。
- [ ] 实现不可变 VO 和纯规则计算器。
- [ ] 重跑测试并确认通过。

### Task 2: Demo/dev 服务与受保护接口

**Files:**
- Create: `backend/src/main/java/com/yilu/yinling/financial/health/FinancialHealthService.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/health/FamilySummaryService.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/health/FamilySummary.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/health/DefaultFinancialHealthService.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/health/DemoFinancialHealthService.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/health/DefaultFamilySummaryService.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/health/DemoFamilySummaryService.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/controller/FinancialHealthController.java`
- Test: `backend/src/test/java/com/yilu/yinling/FinancialHealthTest.java`

**Interfaces:**
- Produces: `GET /api/v1/financial/health-score` 和 `GET /api/v1/financial/family-summary`。

- [ ] 扩展测试：demo 登录后返回评分和家庭摘要，匿名请求返回 401。
- [ ] 运行定向测试并确认缺少 Bean/端点导致失败。
- [ ] 实现 profile 隔离服务和 Controller；dev 缺数据使用清晰默认值。
- [ ] 重跑定向测试并确认通过。

### Task 3: 健康分析前端页面

**Files:**
- Create: `frontend/src/api/health.ts`
- Create: `frontend/src/views/FinancialHealth.vue`
- Modify: `frontend/src/router/index.ts`
- Modify: `frontend/src/views/Dashboard.vue`

**Interfaces:**
- Consumes: 两个 Sprint 12 GET 接口。
- Produces: `/health-score` 页面，含仪表盘、雷达图、风险提示和家庭协同摘要。

- [ ] 定义严格 TypeScript 响应类型及 API 方法。
- [ ] 实现 ECharts 仪表盘和四维雷达图，加入加载、失败、免责声明状态。
- [ ] 注册路由并在 Dashboard 添加“AI决策中心”“金融健康分析”入口。
- [ ] 执行 `npm run build` 并修复类型或构建错误。

### Task 4: Agent 执行动画

**Files:**
- Create: `frontend/src/components/AgentExecutionFlow.vue`
- Modify: `frontend/src/views/FinancialAgent.vue`

**Interfaces:**
- Consumes: 六个固定节点及后端 `agentChain`。
- Produces: `idle -> processing -> completed` 状态动画。

- [ ] 提取节点展示组件，固定稳定尺寸和无障碍状态文本。
- [ ] 请求期间逐节点推进 processing；响应后以真实后端结果完成节点。
- [ ] 失败时停止动画并重置为可重试状态。
- [ ] 执行前端构建。

### Task 5: 文档与全量验证

**Files:**
- Create: `docs/api-sprint-12.md`
- Update artifact: `yinling-demo/yinling-backend.jar`

**Interfaces:**
- Documents: 接口、评分维度、免责声明、Demo 演示顺序。

- [ ] 运行 `mvn clean package`，确认所有后端测试通过。
- [ ] 运行 `npm run build`，确认生产构建成功。
- [ ] 启动 demo JAR，验证登录、健康评分、家庭摘要、Agent 和 health。
- [ ] 同步 JAR 到 `yinling-demo` 并核对哈希。
