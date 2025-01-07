const { createApp,ref,onMounted,markRaw } = Vue
const { createRouter, createWebHashHistory } = VueRouter

const routes = []
const router = createRouter({
  history: createWebHashHistory(),
  routes
})

const app = createApp({
  components: {},
  setup(){
    return {}
  }
})

app.use(ElementPlus)
app.use(router)
app.mount('#app')