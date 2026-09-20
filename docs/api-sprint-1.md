# Sprint 1 API

Base URL: `http://localhost:8080/api/v1`

## 登录

`POST /auth/login`

```json
{"username":"admin","password":"123456"}
```

## 注册

`POST /auth/register`

```json
{"username":"demo","password":"123456","realName":"演示用户","phone":"13800000000"}
```

## 当前用户

`GET /users/me`

请求头：`Authorization: Bearer <token>`

## 修改当前用户

`PUT /users/me`

```json
{"realName":"张老师","phone":"13800000000","avatarUrl":""}
```
