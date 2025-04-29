import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { all } from 'axios'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    AutoImport(({
      resolvers: [ElementPlusResolver()],
    })),
    Components({
      resolvers: [ElementPlusResolver()],
    })
  ],
  optimizeDeps: {
    include: [
      'element-plus',
      '@element-plus/icons-vue',
      'md-editor-v3',
      'echarts'
    ]
  },
  build: {
    chunkSizeWarningLimit: 2000,
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          'markdown': ['md-editor-v3'],
          'echarts': ['echarts']
        }
      }
    }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  base: '/',  // 如果有自己的域名前缀，请配置在这里,
  define: {
    global: 'window'
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    strictPort: true
  },
})
