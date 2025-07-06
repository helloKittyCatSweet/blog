# 🚀 FreeShare 博客系统 —— 基于 Elasticsearch 的智能博客平台

![系统架构图](assets/%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84%E5%9B%BE.drawio.png)

> **2025年哈尔滨工业大学（威海）本科优秀毕业设计**  
> 一个基于 Spring Boot + Vue.js 的现代化全栈博客系统，融合搜索增强、AI摘要、版本控制等创新功能

## 🌟 核心创新

| 传统问题                | 本系统解决方案                     | 技术实现                              |
|-------------------------|-----------------------------|--------------------------------------|
| 搜索效率低下            | ⚡ Elasticsearch 全文搜索        | 毫秒级响应，支持语义化搜索            |
| 版本管理困难            | 🕰️ 增加文章版本表，前端使用嵌入式工具实现版本对比 | 增量式版本存储 + 可视化 diff 工具     |
| 摘要生成不准确          | 🤖 LoRA 微调 DeepSeek 模型      | 92% 摘要准确率（测试数据集）          |
| 推荐效果差              | 🧠 双维度推荐算法                  | 用户画像 + 内容相似度混合推荐         |

## 🛠️ 技术栈全景

### 后端技术矩阵
```mermaid
graph LR
    A[Spring Boot 3] --> B[Spring Security]
    A --> C[Elasticsearch]
    A --> D[Redis]
    A --> E[MySQL 8]
    
    subgraph 日志系统
    F[ELK Stack] --> G[实时日志分析]
    end
    
    subgraph 工作流
    H[Flowable] --> I[审核工作流]
    end
    
    style A fill:#6ab04c,stroke:#333
    style F fill:#f0932b,stroke:#333
    style H fill:#eb4d4b,stroke:#333
 ```

### ⚡ 前端技术矩阵
```mermaid
graph TB
    A[Vue 3] --> B[Composition API]
    A --> C[Vue Router 4]
    A --> D[Pinia 状态管理]
    
    subgraph UI组件
    E[Element Plus] --> F[自适应布局]
    G[ECharts] --> H[数据可视化]
    I[TinyMCE] --> J[富文本编辑]
    end
    
    style A fill:#42b983,stroke:#333
    style E fill:#409eff,stroke:#333
    style G fill:#c23531,stroke:#333
```

 ## 🏗️ 项目结构
```plaintext
blog/
├── 📁 assets/           # 项目资源文件
├── 📁 frontend/         # Vue 3 前端工程
├── 📁 logs/             # 系统日志目录
├── 📁 qwen/             # AI 模型训练代码
├── 📁 scripts/          # 部署脚本集合
│   ├── 🪟 start.ps       # Windows 部署脚本
│   └── 🐧 start.sh      # Linux 部署脚本
├── 📁 src/              # Spring Boot 后端
│   ├── main/java        # 核心代码
│   └── resources        # 配置文件
├── 📁 tools/            # 开发工具集
└── 📄 blog.sql          # 数据库初始化
 ```

 ## 🚀 快速开始
### 📋 环境要求 组件 版本 
Java 17+ 

Maven 3.9.9+ 

MySQL 8.x 

Node.js 22.12.0+ 

npm 11.0.0+ 

Docker 26.1.1+
### ⚙️ 开发环境启动 后端服务
```bash
# 标准启动
mvn spring-boot:run

# 自定义 Redis 配置启动
mvn spring-boot:run -Dspring.redis.password=123456 -Dspring.redis.username=default
 ```
```
 前端服务
```bash
cd frontend
npm install
npm run dev
 ```

### 🌐 生产环境部署
```bash
# Docker Compose 一键部署
docker-compose -f docker-compose.prod.yml up --build
```

### 📚 开发者指南

#### 🔑 关键配置

| 服务组件       | 默认端口 | 访问凭据         | 说明                  |
|----------------|----------|------------------|-----------------------|
| **后端服务**   | `8080`   | admin / 123456   | Spring Boot 主服务    |
| Elasticsearch  | `9200`   | elastic / 123456 | 搜索与分析引擎        |
| Kibana         | `5601`   | elastic / 123456 | 数据可视化平台        |
| Redis          | `6379`   | default / 123456 | 缓存与消息队列        |
| MySQL          | `3306`   | root / 123456    | 主数据库              |

#### 🛠️ 使用说明
1. **端口冲突**：可通过修改 `application.yml` 调整端口
2. **密码安全**：生产环境务必修改默认凭据
3. **服务依赖**：
   ```bash
   # 快速检查服务状态
   curl -X GET http://localhost:9200  # Elasticsearch
   redis-cli -h 127.0.0.1 -p 6379    # Redis
   ```

#### 📌 注意事项
- 所有服务均支持 Docker 部署（见 `docker-compose.yml`）
- Kibana 需先启动 Elasticsearch
- Redis 密码在 `spring.redis.password` 中配置

以下是优化后的表格版本，采用更清晰的结构化展示方式：

### 📊 性能指标
| 指标项         | 性能表现          | 测试条件               |
|----------------|-------------------|------------------------|
| 搜索响应       | < 200ms          | 百万级文档             |
| 系统并发       | 3000+ RPS        | 4核8G服务器            |
| 版本对比       | 0.5s/次          | 100KB文章差异          |

### 🌈 核心功能
#### 用户中心
| 功能图标 | 功能名称         | 技术实现               |
|---------|------------------|------------------------|
| 🔐      | JWT认证授权      | Spring Security + JWT  |
| 👤      | 用户画像构建     | Elasticsearch 聚合分析 |
| 💌      | 站内消息通知     | WebSocket + STOMP      |
| 🤝      | 用户关注互动     | 关系型数据库设计       |

#### 内容管理
| ✍️      | Markdown编辑器   | TinyMCE + 自定义插件   |
|---------|------------------|------------------------|
| 🔍      | 全文搜索         | Elasticsearch 8.x     |
| 🤖      | AI智能摘要       | DeepSeek微调模型      |
| 📊      | 数据统计分析     | ECharts可视化         |

### 🛡️ 安全体系
| 组件          | 功能描述                 | 技术方案                     |
|---------------|--------------------------|------------------------------|
| 🔒 RBAC       | 角色权限控制             | Spring Security + 权限注解   |
| 👮 工作流     | 内容审核流程             | Flowable BPMN引擎            |
| 📝 审计日志   | 操作记录追踪             | Spring Data Envers           |
| 🚫 内容过滤   | 敏感信息拦截             | 百度AI审核+正则匹配          |

### 🔧 系统工具
| 工具类型     | 实现方案                 | 服务提供商        |
|--------------|--------------------------|--------------|
| 📤 文件存储  | 对象存储服务             | 阿里云OSS       |
| 📨 邮件通知  | 事务性邮件发送           | QQ企业邮箱SMTP   |
| 📊 系统监控  | 指标可视化               | ELK → Kibana |

### 🤝 第三方服务
| 服务类型     | 集成方式                 | 应用场景             |
|--------------|--------------------------|----------------------|
| 🤖 AI审核    | REST API调用             | 内容安全检测        |
| 📧 邮件服务  | SMTP协议                | 用户通知            |

### 📈 演进规划
| 版本计划   | 预期效果    | 技术准备                  |
|------------|---------|-----------------------|
| v1.1       | 微服务架构改进 | 微服务模式                 |


备注：如需正常调用百度云内容审核功能，请解除BaiduContentService.java的注释，并解除对应文件中对其的调用即可。代码可以正常工作，但是由于百度云已到期，因此不再调用该API。
Nginx配置在服务器上有，没有同步到本地，之后会加上。