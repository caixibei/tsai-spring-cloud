const {createApp, ref, onMounted, markRaw} = Vue
const {createRouter, createWebHashHistory} = VueRouter

// 路由信息配置
const routes = [
  {
    path: '/dashboard',
    name: 'dashboardPage',
    component: DashboardPage
  },
  {
    path: '/error',
    name: 'errorPage',
    component: ErrorPage
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 应用挂载
const app = createApp({
  components: {DashboardPage,ErrorPage},
  setup() {
    /**表单数据*/
    const loginForm = ref({})
    /**表单校验规则*/
    const rules = ref({
      username: [{required: true, trigger: 'blur', message: '用户名不可为空'}],
      password: [{required: true, trigger: 'blur', message: '密码不可为空'}],
    })

    const login = () => {
      // oauth2
      post('/oauth/token', Qs.stringify(loginForm.value), {
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      }).then(res => {
        console.log(res,'-res-')
      }).catch(error => {
        console.error(error)
      })
    }
    return {loginForm, login, rules}
  }
})

app.use(router)
  .use(ElementPlus, {locale: ElementPlusLocaleZhCn})

app.mount('#app')