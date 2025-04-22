import { defineStore } from 'pinia';

export const useThemeStore = defineStore('theme', {
  state: () => ({
    currentTheme: localStorage.getItem('theme') || 'light'
  }),

  actions: {
    setTheme(theme) {
      this.currentTheme = theme;
      localStorage.setItem('theme', theme);

      // 设置根元素的主题类名
      document.documentElement.className = `theme-${theme}`;

      // 更新 Element Plus 主题
      const html = document.documentElement;
      const prevTheme = html.getAttribute('data-theme');
      if (prevTheme) {
        html.classList.remove(`el-theme-${prevTheme}`);
      }
      html.setAttribute('data-theme', theme);
      html.classList.add(`el-theme-${theme}`);

      // 触发主题变化事件
      window.dispatchEvent(new CustomEvent('theme-change', { detail: theme }));
    }
  }
});