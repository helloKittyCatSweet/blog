# 博客系统

这是一个前后端分离的博客系统，使用 Spring Boot 作为后端，Vue.js 作为前端。

## 项目结构

```
blog/
├── frontend/          # 前端 Vue.js 项目
└── src/              # 后端 Spring Boot 项目
```

## 运行说明

### 后端

1. 确保已安装 Java 17 和 Maven
2. 在项目根目录下运行：
   ```bash
   mvn spring-boot:run
   ```
3. 后端服务将在 http://localhost:8080 启动

### 前端

1. 确保已安装 Node.js (v16+) 和 npm
2. 进入前端项目目录：
   ```bash
   cd frontend
   ```
3. 安装依赖：
   ```bash
   npm install
   ```
4. 启动开发服务器：
   ```bash
   npm run serve
   ```
5. 前端应用将在 http://localhost:3000 启动

## 技术栈

### 后端
- Spring Boot 3.x
- Spring Security + JWT
- Spring Data JPA -> Hibernate Validator
- Spring Data Envers 审计功能
- Redis
- MySQL 8.x
- Lombok
- OpenAPI(Swagger)
- Log4j2
- Flowable 工作流
- WebSocket STOMP基于文本的简单协议 实现实时通讯
- MapStruct

### 前端
- Vue 3 (Composition API)
- Vue Router 4
- Pinia
- Element Plus
- Axios
- Vite
- TinyMCE
- ECharts
- dayjs


### 第三方服务
- 阿里云对象存储服务（OSS）
- 百度文本审核
- QQ邮箱SMTP

## 主要功能

- 用户认证与授权
- 文章管理
- 点赞、收藏、评论
- 用户关注与私信
- 内容审核
- 数据统计
- 系统通知
- 文件上传

### 前端
- Vue.js 3
- Vue Router
- Vuex
- Element Plus
- Axios

## 开发环境配置

### 后端
1. JDK 17+
2. Maven 3.8+
3. MySQL 8.x
4. Redis

### 前端
1. Node.js 16+
2. npm 8+

## 本地运行

### 后端
```bash
mvn spring-boot:run

## API 文档

后端 API 文档可以在启动后端服务后访问：
http://localhost:8080/swagger-ui.html 

### 数据库
application-dev.yml 配置数据库信息后自动建表，支持：

- 数据审计
- 软删除
- 自动时间戳