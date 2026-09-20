# Sprint 4 文本诈骗检测接口

## 文本检测

`POST /api/v1/fraud/detect/text`

需要 JWT 登录，请求：

```json
{"content":"您的养老金账户异常，请点击链接认证"}
```

返回：

```json
{
  "riskLevel":"HIGH",
  "riskScore":66.00,
  "riskTags":["诱导点击","养老诈骗"],
  "explanation":"风险等级为HIGH。...",
  "suggestion":"请立即停止转账或信息提供..."
}
```

评分由规则引擎分数的 60% 与 Mock AI 置信度分数的 40% 融合，等级区间为：0-30 LOW、31-60 MEDIUM、61-85 HIGH、86-100 CRITICAL。每次文本检测都会写入 `fraud_detection_record`，原文、标签、原因和建议均会保存。

## 查询检测记录

`GET /api/v1/fraud/records`

需要 JWT 登录，只返回当前用户的检测记录，按创建时间倒序排列。

## 当前范围

本 Sprint 仅实现文本检测。图片 OCR 接口和后台管理不在本阶段范围内。
