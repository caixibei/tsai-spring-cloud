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
  components: {},
  setup() {
    const formRef = ref()
    /**表单数据*/
    const loginForm = ref({})
    /**表单校验规则*/
    const rules = ref({
      username: [{required: true, trigger: 'blur', message: '用户名不可为空'}],
      password: [{required: true, trigger: 'blur', message: '密码不可为空'}],
      captcha: [{required: true, trigger: 'blur', message: '验证码不可为空'}],
    })
    /**时间戳*/
    const timestamp = ref(0)
    const username_copy = ref('')
    /**登录逻辑*/
    const login = () => {
      formRef.value.validate((valid) => {
        if (valid) {
          post('/login', Qs.stringify(loginForm.value), {
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
          }).then(res => {
            if( res?.data?.code === 500){
              ElementPlus.ElMessage({ message: res?.data?.message + res?.data?.details, type: 'error'})
            }else{
              window.location.href = res?.request?.responseURL
            }
          }).catch(error => {
            console.error(error)
          })
        }
      })
    }
    /**更新时间戳，获取最新的验证码*/
    const refreshTimestamp = () => {
      if(!loginForm.value?.username){
        ElementPlus.ElMessage({ message: '请输入用户名', type: 'warning'})
        return
      }
      username_copy.value = loginForm.value?.username
      timestamp.value = new Date().getTime()
    }
    return {
      formRef,
      loginForm,
      rules,
      timestamp,
      username_copy,
      login,
      refreshTimestamp
    }
  }
})
// 加载 vue-router
app.use(router)
// 加载 Element-Plus，并启用国际化进行汉化
app.use(ElementPlus, {locale: ElementPlusLocaleZhCn})
// 挂载到目标DOM
app.mount('#app')