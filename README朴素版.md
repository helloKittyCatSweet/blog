# FreeShare博客系统——基于Elasticsearch的博客管理系统的设计与实现

本系统系一个基于Spring Boot + Vue.js 采用前后端分离框架的博客管理系统，系2025年哈尔滨工业大学（威海）本科软件工程系优秀毕业设计之一。

## 技术挑战与解决方案
本项目旨在解决传统博客系统在搜索效率上达到瓶颈、文章版本管理存在局限、摘要生成功能存在不足、推荐算法仍有不足的问题提出了对应的解决方案，分别为：  
1）基于Elasticsearch构建全文搜索提高搜索效率  
2）在后端设计文章版本表，在前端嵌入增量型版本对比工具  
3）基于LoRA微调DeepSeek模型提高摘要生成准确性  
4）设计文章推荐算法、用户推荐算法，使用Elasticsearch提升搜索效率

综上，本系统的系统愿景与创新点体现为：  
1）基于Elasticsearch构建全文搜索  
2）基于Elasticsearch实现推荐算法  
3）基于ELK Stack框架实现日志管理  
4）微调DeepSeek改善智能摘要生成功能  
5）文章版本控制

## 系统架构图
![系统架构图.drawio.png](assets/%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84%E5%9B%BE.drawio.png)

## 项目结构

```
blog/
├── assets/            # README中用到的图片附录文件夹
├── frontend/          # 前端 Vue.js 项目
├── logs/              # 日志文件夹，可配置成其它路径，主要在logback.xml里实现
├── qwen/              # 在讯飞星辰MAAS平台上微调时的部分数据集以及图片绘制的准备，语言为Python
├── script             # 脚本文件夹，用于启动和停止服务，实现了Windows和Linux版本，测试偶有失败，会逐渐完善
├── src/               # 后端 Spring Boot 项目
├── tools/             # 工具文件夹，包括内网穿透和代码行数计算的工具
├── blog.sql           # 备份数据库，可用于快速启动项目，当然也可以不使用
└── README.md          # 项目综合介绍
```

## 环境准备
Java 17  
Maven 3.9.9  
MySQL 8.x  
Node.js 22.12.0  
npm 11.0.0  
Docker 26.1.1（如果不想使用Docker，请参考src/main/docker/docker-compose.yml文件中的配置对相关组件进行自行下载）

## 运行说明

### 后端

1. 在项目根目录下运行：
   ```bash
   mvn spring-boot:run
   ```
   或
   ```bash
   mvn spring-boot:run -Dspring.redis.password=123456 -Dspring.redis.username=default -Dspring.redis.host=localhost -Dspring.redis.port=6379
   ```
2. 后端服务将在 http://localhost:8080 启动，当看到日志输出停止时即初始化完成。第一次启动项目时，系统中默认的管理员账户和密码是admin 123456。
3. 后端 API 文档可以在启动后端服务后访问：http://localhost:8080/swagger-ui.html
4. Kibana的仪表盘将默认在5601端口展示，初始化的用户密码是elastic 123456

### 前端

1. 进入前端项目目录：
   ```bash
   cd frontend
   ```
2. 安装依赖：
   ```bash
   npm install
   ```
3. 启动开发服务器：
   ```bash
   npm run serve
   ```
4. 前端应用将在 http://localhost:3000 启动
5. 如部署在服务器上，请自己配置Nginx，yarn build后把dist目录下的所有内容放置在Nginx可访问的路径下。

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
- OCR: Tesseract OCR
- Elasticsearch + Kibana + Logstash 日志监控
- Elasticsearch 全文搜索

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
- Pinia