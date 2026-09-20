# 忆路银龄

基于 AI 智能体的银发群体数字金融陪伴平台，用于“工行杯全国大学生金融科技创新大赛”作品演示。

> 本项目不提供真实银行交易，不构成投资建议，也不承诺收益。

## 核心能力

- Spring Security + JWT 用户认证
- 银龄用户画像与个性化 Prompt
- DeepSeek/Qwen LLM 抽象及 Mock 降级
- RAG 知识库、Embedding 与 pgvector 检索
- 文本与 OCR 图片诈骗风险检测
- AI 养老金融规划与多 Agent 决策链
- 银龄金融健康评分与家庭协同摘要
- 管理端统计与风险趋势分析
- 无数据库、无 Docker、无外部 API 的比赛 Demo 模式

## 技术栈

- 后端：Java 21、Spring Boot 3、Spring Security、MyBatis Plus、MySQL、Redis
- AI：DeepSeek/Qwen、Embedding、RAG、pgvector
- 前端：Vue 3、TypeScript、Vite、Element Plus、ECharts
- 部署：Docker Compose、MinIO、PostgreSQL/pgvector

## Demo 模式

```powershell
cd backend
mvn clean package -DskipTests
java -jar target/yinling-backend.jar --spring.profiles.active=demo
```

Demo 不连接 MySQL、Redis、PostgreSQL、MinIO 或 DeepSeek。

- 账号：`admin`
- 密码：`123456`
- Swagger：`http://localhost:8080/swagger-ui/index.html`
- 健康检查：`http://localhost:8080/api/v1/system/health`

Windows 比赛电脑也可使用 `scripts/start-demo.bat` 启动。

## 开发模式

1. 按需设置 `DEEPSEEK_API_KEY` 等环境变量，参考 `.env.example`。
2. 执行 `docker compose up -d` 启动基础设施。
3. 执行数据库目录中的初始化与增量 SQL。
4. 在 `backend` 执行 `mvn spring-boot:run`。
5. 在 `frontend` 执行 `npm install` 和 `npm run dev`。

## 验证

```powershell
cd backend
mvn test

cd ../frontend
npm run build
```

接口说明位于 `docs/`，数据库脚本位于 `database/`。
