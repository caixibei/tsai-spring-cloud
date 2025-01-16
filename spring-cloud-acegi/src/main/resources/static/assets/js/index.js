const {createApp, ref, onMounted, markRaw} = Vue
const {createRouter, createWebHashHistory} = VueRouter

const routes = []

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

const app = createApp({
  components: {},
  setup() {
    /**表单数据*/
    const loginForm = ref({})
    /**表单校验规则*/
    const rules = ref({
      username: [{required: true, trigger: 'blur', message: '用户名不可为空'}],
      password: [{required: true, trigger: 'blur', message: '密码不可为空'}],
    })
    const login = () => {
      post('/login', Qs.stringify(loginForm.value), {
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      }).then(res => {
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