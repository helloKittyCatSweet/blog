import { defineStore } from 'pinia';

export const useThemeStore = defineStore('free-share-theme', {
  state: () => ({
    currentTheme: localStorage.getItem('app-theme') || 'light'
  }),

  actions: {
    setTheme(theme) {
      this.currentTheme = theme;
      localStorage.setItem('app-theme', theme);

      // 设置根元素的主题类名
      const html = document.documentElement;

      // 移除所有可能的主题类
      html.classList.remove('theme-light', 'theme-dark', 'theme-blue', 'theme-pink', 'theme-green');

      // 添加新主题类
      html.classList.add(`theme-${theme}`);

      // 更新 Element Plus 主题
      if (theme === 'dark') {
        html.classList.add('dark');
      } else {
        html.classList.remove('dark');
      }

      // 设置CSS变量
      this.updateThemeVariables(theme);
    },

    // 更新主题相关的CSS变量
    updateThemeVariables(theme) {
      const root = document.documentElement;

      const themeVariables = {
        light: {
          '--el-bg-color': '#ffffff',
          '--el-text-color-primary': '#303133',
          '--el-border-color': '#dcdfe6',
        },
        dark: {
          '--el-bg-color': '#1a1a1a',
          '--el-text-color-primary': '#ffffff',
          '--el-border-color': '#434343',
        },
        blue: {
          '--el-color-primary': '#409EFF',
          '--el-border-color': '#409EFF',
        },
        pink: {
          '--el-color-primary': '#F5A4C5',
          '--el-border-color': '#F5A4C5',
        },
        green: {
          '--el-color-primary': '#67C23A',
          '--el-border-color': '#67C23A',
        }
      };

      // 应用主题变量
      const variables = themeVariables[theme] || themeVariables.light;
      Object.entries(variables).forEach(([key, value]) => {
        root.style.setProperty(key, value);
      });
    },

    // 初始化主题
    initTheme() {
      const savedTheme = localStorage.getItem('app-theme');
      if (savedTheme) {
        this.setTheme(savedTheme);
      } else {
        this.setTheme('light');
      }
    }
  }
});