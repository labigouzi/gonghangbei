# Sprint 6 工程化增强接口

## OCR与对象存储

`POST /api/v1/fraud/detect/image` 继续使用 `multipart/form-data` 的 `file` 字段。流程为对象存储上传、OCR识别、规则与AI风险分析、记录保存。返回 `ocrContent`、对象存储 URL 和风险结果。

配置：

```yaml
ocr:
  enabled: false
  provider: mock # mock / aliyun / baidu（baidu暂降级Mock）
storage:
  enabled: false
```

`storage.enabled=true` 时使用 MinIO；关闭时返回本地演示 URL，保证 Demo 可运行。阿里云未配置 Key 时自动降级 Mock。

## 时间范围概览

`GET /api/v1/admin/dashboard/overview?startDate=2026-09-01&endDate=2026-09-18`

统计用户、AI会话、诈骗检测和高风险事件，日期为闭区间。

## 风险趋势

`GET /api/v1/admin/fraud/trend?startDate=2026-09-01&endDate=2026-09-18`

返回每日检测数量和高风险数量：

```json
[{"date":"2026-09-01","total":20,"highRisk":5}]
```

所有管理接口仍只允许 ADMIN 角色访问。
