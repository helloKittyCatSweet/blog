import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useUserStore = defineStore("free-share-user", () => {
  const user = ref({
    id: null,
    username: "",
    avatar: "",
    token: "",
    roles: [],
    authorities: [],
  });

  // 登录计算属性
  const isLoggedIn = computed(() => Boolean(user.value.token && user.value.id));

  // Getter 方法
  const getId = () => user.value.id;
  const getUsername = () => user.value.username;
  const getAvatar = () => user.value.avatar;
  const getToken = () => user.value.token;
  const getRoles = () => user.value.roles;
  const getAuthorities = () => user.value.authorities;
  const getUser = () => user.value;


  // 权限检查
  const hasRole = (role) => user.value.roles.some(r => r.authority === role);
  const hasAnyRoles = (roles) => user.value.roles.some(r => roles.includes(r.authority));

  // Setter 方法
  const setId = (newId) => { user.value.id = newId };
  const setUsername = (newUsername) => { user.value.username = newUsername };
  const setAvatar = (newAvatar) => { user.value.avatar = newAvatar };
  const setToken = (newToken) => { user.value.token = newToken };
  // 设置其他用户信息
  const setUser = (userData) => {
    // 确保保留现有token
    const currentToken = user.value.token;
    user.value = {
      ...user.value,
      ...userData,
      token: userData.token || currentToken // 保留现有token或使用新token
    };
    localStorage.setItem('user', JSON.stringify(user.value));
  };
  const removeToken = () => { user.value.token = "" };
  const setRoles = (newRoles) => { user.value.roles = newRoles };
  const setAuthorities = (newAuthorities) => { user.value.authorities = newAuthorities };

  // 清除方法
  const clear = () => {
    user.value = {
      id: null,
      username: "",
      avatar: "",
      token: "",
      roles: [],
      authorities: [],
    };
  };

  // 正确的返回结构
  return {
    user,
    isLoggedIn,
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