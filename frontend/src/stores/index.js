import { createPinia } from 'pinia'
import persist from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(persist)

export default pinia

export * from './modules/user'

export * from './modules/setting'

export * from './modules/message'

export * from './modules/theme'

export * from './modules/favorite'

export * from './modules/post'