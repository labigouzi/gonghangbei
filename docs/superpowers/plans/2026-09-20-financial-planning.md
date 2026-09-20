# Sprint 10 AI养老金融规划中心 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 新增可在dev持久化并在demo零依赖运行的AI养老金融规划中心。

**Architecture:** Controller依赖统一Planning Service接口；dev和demo使用Profile隔离的实现。纯规则计算保持无外部依赖，产品数据源和报告生成按环境替换。

**Tech Stack:** Java 21、Spring Boot 3、Spring Security JWT、MyBatis Plus、MySQL 8、Vue 3、TypeScript、Element Plus、ECharts。

## Global Constraints

- 不重构或破坏现有认证、AI助手、RAG、诈骗检测、OCR、Demo和DeepSeek链路。
- dev使用MySQL与现有LlmClient；demo不访问任何外部服务。
- medicalNeed和travelNeed可空并由后端默认化。
- 产品为比赛模拟数据，不构成购买建议或收益承诺。

---

### Task 1: 规划规则与返回契约

**Files:**
- Create: `backend/src/test/java/com/yilu/yinling/FinancialPlanTest.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/dto/FinancialPlanRequest.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/vo/FinancialPlanResponse.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/service/FinancialPlanCalculator.java`

**Interfaces:**
- Produces: `FinancialPlanCalculator.calculate(FinancialPlanRequest)` and response allocation/risk/stage types.

- [ ] Write tests asserting 68/6000/500000/稳健 returns退休初期、LOW and amounts100000/300000/100000.
- [ ] Run `mvn -Dtest=FinancialPlanTest test` and verify compilation fails because types do not exist.
- [ ] Implement validated request, response records and deterministic calculator.
- [ ] Re-run the focused test and verify PASS.

### Task 2: 产品匹配和数据库模型

**Files:**
- Create: `backend/src/main/java/com/yilu/yinling/financial/entity/FinancialProduct.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/entity/ElderlyFinancialPlan.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/mapper/FinancialProductMapper.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/mapper/ElderlyFinancialPlanMapper.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/service/ProductRecommendationService.java`
- Create: dev/demo service implementations and `FinancialMapperConfig.java`
- Create: `database/update_sprint10.sql`

**Interfaces:**
- Produces: `recommend(age, riskPreference, retirementGoal)` returning response product recommendations.

- [ ] Extend failing tests for low-risk priority and high-risk recommendation limits.
- [ ] Implement entities, mappers, profile-isolated services and 10+ idempotent product inserts.
- [ ] Run focused tests and verify PASS.

### Task 3: AI报告和规划编排

**Files:**
- Create: `backend/src/main/java/com/yilu/yinling/financial/prompt/FinancialPromptTemplate.java`
- Create: `backend/src/main/java/com/yilu/yinling/financial/service/FinancialPlanningService.java`
- Create: dev and demo implementations under `service/impl`

**Interfaces:**
- Consumes: calculator, recommendations, `LlmClient` in dev.
- Produces: `generate(userId, request)` returning `FinancialPlanResponse`.

- [ ] Add failing tests for six report headings, five financialChain steps and demo provider-independent output.
- [ ] Implement dev persistence/LLM fallback and demo in-memory plan IDs/template report.
- [ ] Run focused tests and verify PASS.

### Task 4: JWT REST接口

**Files:**
- Create: `backend/src/main/java/com/yilu/yinling/financial/controller/FinancialPlanController.java`
- Extend: `backend/src/test/java/com/yilu/yinling/FinancialPlanTest.java`

**Interfaces:**
- Produces: authenticated `POST /api/v1/financial/plan` returning unified `Result<FinancialPlanResponse>`.

- [ ] Add MockMvc failing tests for JWT success and unauthenticated 401.
- [ ] Implement controller with OpenAPI annotations and current authentication details.
- [ ] Run focused tests and verify PASS.

### Task 5: 前端规划中心

**Files:**
- Create: `frontend/src/api/financial.ts`
- Create: `frontend/src/views/FinancialPlanning.vue`
- Modify: `frontend/src/router/index.ts`
- Modify: `frontend/src/views/Dashboard.vue`

**Interfaces:**
- Consumes: `/api/v1/financial/plan` response.

- [ ] Add typed API models and call wrapper.
- [ ] Implement accessible form, chain steps, ECharts allocation chart, report and recommendations.
- [ ] Add `/financial` route and Dashboard entry.
- [ ] Run `npm run build` and resolve all TypeScript/build errors.

### Task 6: 文档与全量验证

**Files:**
- Create: `docs/api-sprint-10.md`

- [ ] Document request/response, SQL tables, dev/demo chain and safety wording.
- [ ] Run `mvn test -q` and confirm exit code 0.
- [ ] Run `npm run build` and confirm exit code 0.
- [ ] Package Demo JAR and smoke-test login plus financial endpoint under `demo` profile.
