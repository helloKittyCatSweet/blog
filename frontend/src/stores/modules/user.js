import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore("free-share-user", () => {
  const user = ref({
    id: "",
    username: "",
    avatar: "",
    settings: {},
    token: "",
    roles: [],
    authorities: [],
  });

  // Getter 方法
  const getId = () => user.value.id;
  const getUsername = () => user.value.username;
  const getAvatar = () => user.value.avatar;
  const getSettings = () => user.value.settings;
  const getToken = () => user.value.token;
  const getRoles = () => user.value.roles;
  const getAuthorities = () => user.value.authorities;
  const getUser = () => user.value;

  // 权限检查
  const hasRole = (role) => user.value.roles.includes(role);
  const hasAnyRoles = (roles) => user.value.roles.some(role => roles.includes(role));

  // Setter 方法
  const setId = (newId) => { user.value.id = newId };
  const setUsername = (newUsername) => { user.value.username = newUsername };
  const setAvatar = (newAvatar) => { user.value.avatar = newAvatar };
  const setSettings = (newSettings) => { user.value.settings = newSettings };
  const setToken = (newToken) => { user.value.token = newToken };
  // 设置其他用户信息
  const setUser = (userData) => {
    user.value = { ...user.value, ...userData }
    localStorage.setItem('user', JSON.stringify(user.value))
  };
  const removeToken = () => { user.value.token = "" };
  const setRoles = (newRoles) => { user.value.roles = newRoles };
  const setAuthorities = (newAuthorities) => { user.value.authorities = newAuthorities };

  // 清除方法
  const clear = () => {
    user.value = {
      id: "",
      username: "",
      avatar: "",
      settings: {},
      token: "",
      roles: [],
      authorities: [],
    };
  };

  // 正确的返回结构
  return {
    user,
    getUser,
    setUser,
    hasRole,
    hasAnyRoles,
    getId,
    setId,
    getUsername,
    setUsername,
    getAvatar,
    setAvatar,
    getSettings,
    setSettings,
    getToken,
    setToken,
    removeToken,
    getRoles,
    setRoles,
    getAuthorities,
    setAuthorities,
    clear
  };
}, {
  persist: {
    key: 'free-share-user',
    storage: localStorage,
    paths: ['user']
  }
});