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
              <p>Kitty Blog 是一个现代化的博客系统，致力于为用户提供简洁、高效的写作和阅读体验。采用 Vue 3 + Spring Boot 技术栈开发，具有响应式设计和丰富的功能特性。</p>
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
                    <p>支持邮箱注册、GitHub 登录等多种认证方式，提供个性化的用户配置。</p>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card class="feature-card" shadow="hover">
                    <template #header>
                      <div class="feature-header">
                        <el-icon><Edit /></el-icon>
                        <span>文章管理</span>
                      </div>
                    </template>
                    <p>支持 Markdown 编辑、草稿保存、文章分类、标签管理等功能。</p>
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
                    <p>支持文章评论、点赞、收藏等社交功能，促进用户交流。</p>
                  </el-card>
                </el-col>
              </el-row>
            </section>

            <el-divider />

            <section class="tech-section">
              <h2>技术栈</h2>
              <div class="tech-list">
                <el-tag size="large" effect="plain" class="tech-tag">Vue 3</el-tag>
                <el-tag size="large" effect="plain" class="tech-tag">Element Plus</el-tag>
                <el-tag size="large" effect="plain" class="tech-tag">Spring Boot</el-tag>
                <el-tag size="large" effect="plain" class="tech-tag">MySQL</el-tag>
                <el-tag size="large" effect="plain" class="tech-tag">Redis</el-tag>
              </div>
            </section>

            <el-divider />

            <section class="contact-section">
              <h2>联系我们</h2>
              <p>如果您有任何问题或建议，欢迎通过以下方式联系我们：</p>
              <div class="contact-info">
                <p><el-icon><Message /></el-icon> 邮箱：1448185924@qq.com</p>
                <p><el-icon><Link /></el-icon> GitHub：<a href="https://github.com/helloKittyCatSweet/blog" target="_blank">https://github.com/helloKittyCatSweet/blog</a></p>
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
  }

  .tech-list {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    .tech-tag {
      font-size: 14px;
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
        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
</style>