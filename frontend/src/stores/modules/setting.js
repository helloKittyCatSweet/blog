import { defineStore } from "pinia";
import { ref } from "vue";

export const useSettingsStore = defineStore("free-share-settings", () => {
  const settings = ref({
    theme: 'light',
    notifications: true,
    githubAccount: "",
    csdnAccount: "",
    bilibiliAccount: "",
  });

  // Getter 方法
  const getTheme = () => settings.value.theme;
  const getSettings = () => settings.value;

  // Setter 方法
  const setTheme = (newTheme) => {
    settings.value.theme = newTheme;
    document.documentElement.className = `theme-${newTheme}`;
  };

  const setSettings = (newSettings) => {
    settings.value = { ...newSettings };
  };

  // 重置方法
  const resetSettings = () => {
    settings.value = {
      theme: 'light',
      notifications: true,
      githubAccount: "",
      csdnAccount: "",
      bilibiliAccount: "",
    };
  };

  return {
    settings,
    getTheme,
    setTheme,
    getSettings,
    setSettings,
    resetSettings,
  };
}, {
  persist: {
    key: 'free-share-settings',
    storage: localStorage,
    paths: ['settings']
  }
});