<template>
  <div class="about-container">
    <el-row :gutter="20">
      <el-col :span="16" :offset="4">
        <el-card class="about-card" shadow="never">
          <template #header>
            <div class="about-header">
              <h1>关于 FreeShare</h1>
            </div>
          </template>

          <div class="about-content">
            <section class="intro-section">
              <h2>项目介绍</h2>
              <p>FreeShare 是一个现代化的技术博客分享平台，专注于为开发者提供优质的技术内容分享和交流空间。采用前后端分离架构，基于 Vue 3 + Spring Boot 开发，具有响应式设计和丰富的交互功能。</p>
            </section>

            <el-divider />

            <section class="feature-section">
              <h2>主要特性</h2>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-card class="feature-card" shadow="hover">
                    <template #header>
                      <div class="feature-header">
                        <el-icon><User /></el-icon>
                        <span>用户系统</span>
                      </div>
                    </template>
                    <ul class="feature-list">
                      <li>支持用户名密码、GitHub OAuth2 登录</li>
                      <li>邮箱验证和密码重置功能</li>
                      <li>个性化主题设置（明暗主题）</li>
                      <li>用户活动追踪和数据统计</li>
                    </ul>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card class="feature-card" shadow="hover">
                    <template #header>
                      <div class="feature-header">
                        <el-icon><Edit /></el-icon>
                        <span>内容管理</span>
                      </div>
                    </template>
                    <ul class="feature-list">
                      <li>Markdown 编辑器支持</li>
                      <li>文章分类和标签管理</li>
                      <li>文章版本控制系统</li>
                      <li>文章导出和分享功能</li>
                      <li>草稿自动保存功能</li>
                    </ul>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card class="feature-card" shadow="hover">
                    <template #header>
                      <div class="feature-header">
                        <el-icon><Share /></el-icon>
                        <span>社交互动</span>
                      </div>
                    </template>
                    <ul class="feature-list">
                      <li>文章点赞和收藏功能</li>
                      <li>多级评论系统</li>
                      <li>评论点赞和回复功能</li>
                      <li>文章举报处理机制</li>
                      <li>用户关注和互动系统</li>
                    </ul>
                  </el-card>
                </el-col>
              </el-row>
            </section>

            <el-divider />

            <section class="tech-section">
              <h2>技术栈</h2>
              <div class="tech-stack">
                <div class="tech-category">
                  <h3>前端技术</h3>
                  <div class="tech-list">
                    <el-tag size="large" effect="plain" class="tech-tag">Vue 3</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Vue Router</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Pinia</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Element Plus</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">MD Editor V3</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Axios</el-tag>
                  </div>
                </div>
                <div class="tech-category">
                  <h3>后端技术</h3>
                  <div class="tech-list">
                    <el-tag size="large" effect="plain" class="tech-tag">Spring Boot</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Spring Security</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Hibernate</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">MySQL</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Redis</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">JWT</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Elasticsearch</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">ELK</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">AOP</el-tag>
                  </div>
                </div>
                <div class="tech-category">
                  <h3>系统支持协议</h3>
                  <div class="tech-list">
                    <el-tag size="large" effect="plain" class="tech-tag">HTTP</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">STOMP</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">SMTP</el-tag>
                  </div>
                </div>
                <div class="tech-category">
                  <h3>开发工具</h3>
                  <div class="tech-list">
                    <el-tag size="large" effect="plain" class="tech-tag">Git</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Maven</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Docker</el-tag>
                    <el-tag size="large" effect="plain" class="tech-tag">Nginx</el-tag>
                  </div>
                </div>
              </div>
            </section>

            <el-divider />

            <section class="contact-section">
              <h2>联系我们</h2>
              <p>如果您有任何问题、建议或发现了 bug，欢迎通过以下方式联系我们：</p>
              <div class="contact-info">
                <p>
                  <el-icon><Message /></el-icon> 邮箱：1448185924@qq.com
                </p>
                <p>
                  <el-icon><Link /></el-icon> GitHub：
                  <a href="https://github.com/helloKittyCatSweet/blog" target="_blank" rel="noopener noreferrer">
                    https://github.com/helloKittyCatSweet/blog
                  </a>
                </p>
              </div>
            </section>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { User, Edit, Share, Message, Link } from '@element-plus/icons-vue'
</script>

<style lang="scss" scoped>
.about-container {
  padding: 40px 20px;
  background-color: var(--el-bg-color-page);

  .about-card {
    .about-header {
      text-align: center;
      h1 {
        margin: 0;
        color: var(--el-text-color-primary);
        font-size: 28px;
      }
    }

    .about-content {
      section {
        margin-bottom: 30px;

        h2 {
          margin-bottom: 20px;
          color: var(--el-text-color-primary);
          font-size: 22px;
        }

        h3 {
          margin: 16px 0;
          color: var(--el-text-color-primary);
          font-size: 18px;
        }

        p {
          color: var(--el-text-color-regular);
          line-height: 1.8;
        }
      }
    }
  }

  .feature-card {
    height: 100%;
    
    .feature-header {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      .el-icon {
        font-size: 18px;
      }
    }

    .feature-list {
      list-style: none;
      padding: 0;
      margin: 0;
      
      li {
        position: relative;
        padding-left: 16px;
        margin-bottom: 8px;
        color: var(--el-text-color-regular);
        line-height: 1.6;

        &::before {
          content: "•";
          position: absolute;
          left: 0;
          color: var(--el-color-primary);
        }

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }

  .tech-stack {
    .tech-category {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }

  .tech-list {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    
    .tech-tag {
      font-size: 14px;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
    }
  }

  .contact-info {
    margin-top: 20px;
    
    p {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 12px 0;
      
      .el-icon {
        color: var(--el-color-primary);
      }
      
      a {
        color: var(--el-color-primary);
        text-decoration: none;
        transition: color 0.3s ease;
        
        &:hover {
          color: var(--el-color-primary-light-3);
          text-decoration: underline;
        }
      }
    }
  }
}
</style>