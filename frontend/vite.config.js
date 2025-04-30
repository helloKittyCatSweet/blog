import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

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
      onwarn(warning, warn) {
        // 添加构建警告日志
        console.log('Build warning:', warning);
        warn(warning);
      },
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          'markdown': ['md-editor-v3'],
          'echarts': ['echarts']
        },
         // 添加资源文件类型配置
         assetFileNames: (assetInfo) => {
          const info = assetInfo.name.split('.');
          let extType = info[info.length - 1];
          if (/\.(mp4|webm|ogg|mp3|wav|flac|aac)(\?.*)?$/i.test(assetInfo.name)) {
            extType = 'media';
          } else if (/\.(png|jpe?g|gif|svg|ico|webp)(\?.*)?$/i.test(assetInfo.name)) {
            extType = 'img';
          } else if (/\.(woff2?|eot|ttf|otf)(\?.*)?$/i.test(assetInfo.name)) {
            extType = 'fonts';
          }
          return `assets/${extType}/[name]-[hash][extname]`;
        },
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js'
      }
    }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
    extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue', '.scss']
  },
  css: {
    preprocessorOptions: {
      scss: {
        charset: false
      }
    }
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
