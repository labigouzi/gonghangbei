# 忆路银龄比赛现场 Demo

## 启动要求

- Windows
- Java 21 或更高版本
- 无需 Docker、MySQL、Redis、PostgreSQL、MinIO 或 DeepSeek API Key

## 启动

双击 `start-demo.bat`，或在当前目录执行：

```powershell
java -jar yinling-backend.jar --spring.profiles.active=demo
```

## 访问地址

- Swagger：http://localhost:8080/swagger-ui/index.html
- Health：http://localhost:8080/api/v1/system/health

## 演示账号

账号 `admin`，密码 `123456`。登录后可使用 AI 养老/银龄旅行助手；Demo 使用关键词回答，不调用外部大模型。
