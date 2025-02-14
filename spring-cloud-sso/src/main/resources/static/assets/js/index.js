const {createApp, ref, onMounted, markRaw} = Vue
const {createRouter, createWebHashHistory} = VueRouter

// 路由信息配置
const routes = []
const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 应用挂载
const app = createApp({
  setup() {
    return {}
  }
})
// 加载 vue-router
app.use(router)
// 加载 Element-Plus，并启用国际化进行汉化
app.use(ElementPlus, {locale: ElementPlusLocaleZhCn})
// 挂载到目标DOM
app.mount('#app')