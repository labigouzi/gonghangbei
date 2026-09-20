# Sprint 11 AI银龄金融智能体 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建可解释、可视化、dev/demo隔离的AI银龄金融多Agent编排链。

**Architecture:** AgentContext承载单次请求状态，独立Agent依次完成意图、画像、风险、RAG、规划、产品和报告。Orchestrator保证单次外部调用并返回可观测步骤。

**Tech Stack:** Java 21、Spring Boot 3、JWT、MyBatis Plus、现有LlmClient/RagService、Vue 3、TypeScript、Element Plus、ECharts。

## Global Constraints

- 不改变Sprint 10、DeepSeek、诈骗、OCR和Demo既有接口或实现语义。
- dev允许数据库/RAG/LLM；demo不访问外部服务。
- 产品匹配和LLM各最多调用一次。
- 风险等级阈值为0-30 LOW、31-60 MEDIUM、61-100 HIGH。

---

### Task 1: Agent契约、意图和风险模型

**Files:**
- Create: `backend/src/test/java/com/yilu/yinling/FinancialAgentTest.java`
- Create: `backend/src/main/java/com/yilu/yinling/ai/agent/model/AgentModels.java`
- Create: `backend/src/main/java/com/yilu/yinling/ai/agent/IntentRecognitionAgent.java`
- Create: `backend/src/main/java/com/yilu/yinling/ai/agent/RiskScoreCalculator.java`
- Create: `backend/src/main/java/com/yilu/yinling/ai/agent/RiskAssessmentAgent.java`

**Interfaces:**
- Produces `recognize(String)`, `calculate(FinancialPlanRequest)` and immutable intent/risk records.

- [ ] Write failing tests for five intents and score boundaries including Demo 28/LOW.
- [ ] Run focused test and confirm missing-type failure.
- [ ] Implement minimal deterministic models and agents.
- [ ] Re-run and confirm PASS.

### Task 2: Profile extraction and context

**Files:**
- Create: `AgentContext.java`, `UserProfileAgent.java`, dev/demo data provider implementations.

**Interfaces:**
- Produces normalized FinancialPlanRequest, financial profile, assumptions and clarification flag.

- [ ] Add failing tests for “68岁、50万、6000退休金、稳健” and fallback ordering.
- [ ] Implement Chinese amount parsing and profile/history/default providers with Profile isolation.
- [ ] Verify focused tests PASS.

### Task 3: RAG、产品、报告Agent与编排器

**Files:**
- Create: `KnowledgeRetrievalAgent.java`, `ProductMatchAgent.java`, `ReportGenerationAgent.java`.
- Create: `FinancialAgentOrchestrator.java` and dev/demo report generator implementations.
- Create response DTO/VO under `financial/dto` and `financial/vo`.

**Interfaces:**
- Produces `FinancialAgentResponse orchestrate(Long, String)` with six ordered steps.

- [ ] Add failing orchestration tests asserting sequence, sources and single invocation counts.
- [ ] Implement AgentContext pipeline using FinancialPlanCalculator without FinancialPlanningService.generate.
- [ ] Verify focused tests PASS.

### Task 4: JWT REST接口与Demo集成

**Files:**
- Create: `FinancialAgentController.java`.
- Create: `FinancialAgentControllerTest.java` and `FinancialAgentDemoIntegrationTest.java`.

- [ ] Add failing tests for authenticated success, unauthenticated 401 and demo fixed result.
- [ ] Implement validated endpoint and OpenAPI examples.
- [ ] Verify controller and demo tests PASS.

### Task 5: Agent可视化前端

**Files:**
- Create: `frontend/src/api/agent.ts`.
- Create: `frontend/src/views/FinancialAgent.vue`.
- Modify: router and Dashboard.

- [ ] Add typed API contract.
- [ ] Implement question input, six-node status view, risk gauge, allocation chart, RAG and report.
- [ ] Add `/agent` route and Dashboard entry.
- [ ] Run `npm run build` and fix errors.

### Task 6: 文档与交付验证

**Files:**
- Create: `docs/api-sprint-11.md`.

- [ ] Document architecture diagram, endpoint, risk model, RAG and Demo behavior.
- [ ] Run `mvn test -q` and confirm exit 0.
- [ ] Run `npm run build` and confirm exit 0.
- [ ] Package and smoke-test Demo login plus agent endpoint; update `yinling-demo/yinling-backend.jar`.
