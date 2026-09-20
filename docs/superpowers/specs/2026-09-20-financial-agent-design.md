# Sprint 11 AI银龄金融智能体设计

## 目标

在Sprint 10规划能力之上增加可解释、可观测的多Agent编排层。每个Agent有独立输入输出和职责，编排只调用一次产品匹配、一次RAG检索和至多一次LLM，不改变现有DeepSeek、规划、诈骗、OCR或Demo链路。

## 环境隔离

- dev：读取用户画像和最近规划，使用现有RAG、ProductRecommendationService和LlmClient。
- demo：使用固定68岁、资产50万元、退休金6000元、稳健偏好；使用Mock RAG、内存产品和本地报告，不访问任何外部服务。
- Sprint 10接口和实现保持兼容。

## AgentContext

一次请求共享以下状态：userId、原始问题、提取/补齐的FinancialPlanRequest、assumptions、intent、profile、risk、allocation、recommendations、knowledgeSources、answer和agentChain。Agent只能填写自己负责的字段。

## Agent职责

1. IntentRecognitionAgent：关键词规则识别RETIREMENT_PLAN、FINANCIAL_CONSULTATION、RISK_CONSULTATION、FRAUD_DETECTION、TRAVEL_RETIREMENT并计算置信度。
2. UserProfileAgent：从消息提取年龄、万元/元资产、月收入与风险偏好；缺失项依次使用用户画像、最近规划和明示默认值。年龄或资产完全无法取得时产生澄清回答。
3. RiskAssessmentAgent：调用RiskScoreCalculator，输出0-100、LOW/MEDIUM/HIGH及警告。
4. KnowledgeRetrievalAgent：调用RagService.retrieveDetailed，输出标题、chunk和similarity。
5. ProductMatchAgent：调用ProductRecommendationService一次，生成推荐理由和风险提示。
6. ReportGenerationAgent：dev调用LlmClient一次并支持模板回退；demo只使用模板。

资金规划步骤复用FinancialPlanCalculator，并作为可观测Agent节点呈现，不调用FinancialPlanningService.generate，避免重复产品匹配和LLM调用。

## 风险模型

RiskScoreCalculator根据年龄、资产、月收入和风险偏好加权并限制在0-100。等级统一为0-30 LOW、31-60 MEDIUM、61-100 HIGH。Demo样例得分固定为28/LOW。警告至少包含高收益承诺、应急资金和官方渠道三类中的相关项。

## 编排与响应

`POST /api/v1/financial/agent/chat`要求JWT，请求为`{"message":"..."}`。Orchestrator按意图识别、用户画像、风险评估、资金规划、产品匹配、智能报告顺序执行，每步记录name、status、summary和durationMs。

响应包含answer、intent、intentConfidence、agentChain、financialAnalysis、knowledgeSources。financialAnalysis包含画像、风险、资金分配、产品和assumptions。

## 前端

新增`/agent`：大字体问题输入、六节点真实状态动画、风险仪表盘、资金用途图、产品建议、RAG来源和报告。Dashboard新增“AI金融智能体”入口。使用Element Plus和ECharts，移动端使用单列布局。

## 错误处理

- 空问题由Bean Validation返回400。
- 无JWT返回401。
- 关键参数不足返回澄清回答和PARTIAL状态，不伪造规划。
- RAG失败时knowledgeSources为空并记录降级摘要。
- LLM失败时使用本地报告，Agent状态仍为completed并标记fallback。

## 测试

覆盖五类意图、中文金额提取、风险边界及Demo 28分、六步顺序、单次产品/RAG/LLM调用、知识来源、JWT拒绝和Demo完整闭环。最终执行`mvn test -q`、`npm run build`及Demo JAR冒烟测试。
