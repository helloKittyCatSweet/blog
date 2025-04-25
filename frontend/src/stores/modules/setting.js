import { defineStore } from "pinia";
import { ref } from "vue";
import { useThemeStore } from "./theme";

export const useSettingsStore = defineStore("free-share-settings", () => {
  const settings = ref({
    theme: localStorage.getItem('app-theme') || 'light',
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
    // 同步更新 theme store
    const themeStore = useThemeStore();
    themeStore.setTheme(newTheme);
  };

  const setSettings = (newSettings) => {
    settings.value = { ...newSettings };
    // 如果设置中包含主题，同步更新主题
    if (newSettings.theme) {
      setTheme(newSettings.theme);
    }
  };

  // 重置方法
  const resetSettings = () => {
    const defaultSettings = {
      theme: 'light',
      notifications: true,
      githubAccount: "",
      csdnAccount: "",
      bilibiliAccount: "",
    };
    settings.value = defaultSettings;
    setTheme(defaultSettings.theme);
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