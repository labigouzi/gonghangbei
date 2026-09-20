# Sprint 10 AI养老金融规划中心设计

## 目标

在不改变认证、AI助手、RAG、诈骗检测、OCR、DeepSeek和Demo既有行为的前提下，新增从用户输入到养老资金规划、模拟产品匹配和AI报告的完整决策链。

## 环境边界

- `dev`：使用MySQL持久化产品与规划记录，通过现有`LlmClient`生成报告，保留DeepSeek及其Mock降级。
- `demo`：不加载financial Mapper，不访问MySQL、Redis、PostgreSQL、MinIO或DeepSeek；使用内存产品、确定性规划规则和模板报告。
- Controller和响应契约在两个环境保持一致。

## 后端架构

新增`com.yilu.yinling.financial`模块，包含controller、service、service.impl、entity、mapper、dto、vo和prompt。`FinancialPlanningService`作为统一入口，dev和demo分别提供Profile实现。`ProductRecommendationService`按同样方式隔离数据库与内存产品来源。

请求通过JWT取得`userId`。规划流程为：参数默认化 -> 养老阶段判断 -> 风险等级映射 -> 20/60/20资金用途分配 -> 产品匹配 -> 六段式报告生成 -> dev保存规划记录 -> 返回结果。

## 规划规则

- 60至69岁：退休初期。
- 70岁及以上：养老保障阶段。
- 60岁以下：退休准备阶段。
- 稳健或低风险映射为`LOW`，平衡映射为`MEDIUM`，激进映射为`HIGH`；未知值默认`LOW`。
- 资产用途：生活备用资金20%、养老保障资金60%、灵活消费资金20%。不计算或承诺投资收益。
- `medicalNeed`和`travelNeed`为可选文本；空值分别默认“暂无特别医疗需求”和“暂无特别旅行需求”。

## 产品匹配

产品表提供不少于10条比赛模拟数据。匹配首先过滤年龄区间，再按风险偏好选择。低风险老人优先个人养老金、养老储蓄和养老保险；稳健用户可增加稳健型养老理财；高风险输入仍减少推荐数量并突出风险提示。所有推荐都标注“仅作知识演示，不构成购买建议”。

## AI报告

`FinancialPromptTemplate`生成固定结构Prompt：

1. 【用户情况】
2. 【养老需求分析】
3. 【资金规划建议】
4. 【金融产品建议】
5. 【风险提醒】
6. 【温馨提示】

语言适老化，避免复杂术语，不代替金融机构决策，不承诺收益。dev调用现有`LlmClient`；异常时返回本地模板报告。demo始终使用本地模板报告。

## API和前端

`POST /api/v1/financial/plan`要求JWT，请求包含年龄、退休收入、资产规模、风险偏好、养老目标以及可选医疗/旅行需求。响应包含`planId`、风险等级、阶段、摘要、资金分配、推荐产品、报告、风险提示和`financialChain`。

新增`/financial`页面，使用Element Plus表单、ECharts环形图、步骤条、报告和产品列表。Dashboard新增“AI养老金融规划”入口。

## 数据库

新增`financial_product`和`elderly_financial_plan`。产品数据明确为模拟信息，不宣传真实收益。规划表保存输入快照和最终报告文本。

## 错误与安全

- Bean Validation处理年龄、收入、资产及必填字段。
- 未登录请求由现有Spring Security返回401。
- LLM失败不导致规划失败，回退为确定性模板报告。
- Demo不实例化数据库Mapper或真实LLM规划服务。

## 测试

- 规则单测覆盖阶段、风险、默认需求和20/60/20分配。
- 产品匹配测试覆盖低风险优先级与高风险缩减。
- Controller测试覆盖JWT访问和未认证拒绝。
- Demo Profile测试验证固定演示输入、Mock报告和无数据库依赖启动。
- 最终执行`mvn test -q`与`npm run build`。
