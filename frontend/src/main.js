import { createApp } from 'vue'

import App from './App.vue'
import router from './router'
import pinia from './stores'

// fontawesome
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faEnvelope } from '@fortawesome/free-solid-svg-icons'

import { permissionDirective } from '@/directives/permission'



library.add(faEnvelope);

const app = createApp(App)

app.use(pinia)
app.use(router)

app.component('font-awesome-icon', FontAwesomeIcon)
app.directive('permission', permissionDirective)

app.mount('#app')


