// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/main.js
// hangu: 0 行 | kelei: 36 行 | 本文件合计: 36 行
// ============================================================
// @contributor kelei - main (36 行)
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')










