# Sprint 5 OCR 与管理端接口

## 图片诈骗检测

`POST /api/v1/fraud/detect/image`

需要 JWT，使用 `multipart/form-data` 上传字段 `file`。当前限制为图片类型、最大 5MB。接口先调用 `OcrClient`，再复用 Sprint 4 的规则引擎、Mock AI 分析和风险评分，返回：

```json
{
  "ocrContent":"您的养老金账户异常，请点击链接认证",
  "fileUrl":"pension.png",
  "result": {
    "riskLevel":"HIGH",
    "riskScore":66.00,
    "riskTags":["养老诈骗","诱导点击"],
    "explanation":"...",
    "suggestion":"..."
  }
}
```

OCR 当前由 `MockOcrClient` 提供，后续可替换百度、腾讯或阿里云实现。

## 检测历史

`GET /api/v1/fraud/records`

登录用户只能查询自己的文本和图片检测记录，图片记录额外返回 `fileUrl`、`ocrContent`。

## 管理统计

以下接口仅允许 ADMIN 角色访问，普通用户返回 HTTP 403：

- `GET /api/v1/admin/dashboard/overview`：用户数、AI咨询数、诈骗检测数、高风险事件数。
- `GET /api/v1/admin/fraud/statistics`：风险等级和风险标签统计。
- `GET /api/v1/admin/profile/statistics`：风险偏好、数字金融能力统计。

管理员角色在登录时从 `sys_user_role` 读取并写入 JWT，Spring Security 使用 `ROLE_ADMIN` 进行权限判断。
