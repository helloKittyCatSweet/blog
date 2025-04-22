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
      '@element-plus/icons-vue'
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
    },
    chunkSizeWarningLimit: 2000
  },
  optimizeDeps: {
    include: ['element-plus', 'md-editor-v3', 'echarts']
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
  // server: {
  //   host: '0.0.0.0',
  //   port: 5173,
  //   strictPort: false,
  //   allowedHosts: [
  //     'localhost',
  //     '.ngrok-free.app',
  //     '.cpolar.cn',
  //     '.r3.cpolar.cn',    // 添加新的域名
  //     '2a646197.r3.cpolar.cn'  // 添加具体的主机
  //   ],
  //   proxy: {
  //     '/api': {
  //       target: 'https://6e736d3c.r18.cpolar.top',
  //       changeOrigin: true,
  //       secure: true,
  //       rewrite: (path) => path.replace(/^\/api/, '')
  //     },
  //     '/ip': {  // 添加 IP 服务的代理
  //       target: 'https://api.ipify.org',
  //       changeOrigin: true,
  //       secure: true,
  //       rewrite: (path) => path.replace(/^\/ip/, '')
  //     }
  //   },
  // }
})
