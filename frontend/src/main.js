import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './stores'

// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// fontawesome
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faEnvelope } from '@fortawesome/free-solid-svg-icons'

import { permissionDirective } from '@/directives/permission'

import '@/styles/themes.css'

library.add(faEnvelope);

const app = createApp(App)

// Register Element Plus Icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}


app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})

app.component('font-awesome-icon', FontAwesomeIcon)
app.directive('permission', permissionDirective)

app.mount('#app')