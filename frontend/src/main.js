import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './stores'

// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// fontawesome
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faEnvelope } from '@fortawesome/free-solid-svg-icons'

import { permissionDirective } from '@/directives/permission'

// Github icon
import { faGithub } from '@fortawesome/free-brands-svg-icons';

import '@/styles/themes.css'

library.add(faEnvelope);
library.add(faGithub);

const app = createApp(App)

// Register Element Plus Icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}


app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
  zIndex: 3000,
  size: 'default',
  namespace: 'el'  // 添加命名空间
})

app.component('font-awesome-icon', FontAwesomeIcon)
app.directive('permission', permissionDirective)

app.mount('#app')

// 预加载常用组件
const preloadComponents = () => {
  const components = [
    import('@/views/blog/Home.vue'),
    import('@/views/blog/PostList.vue'),
    import('@/views/blog/Categories.vue'),
    import('@/views/blog/Tags.vue')
  ]
  return Promise.all(components)
}

// 在应用启动时预加载
preloadComponents()