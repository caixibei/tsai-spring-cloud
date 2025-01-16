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
    const loginForm = ref({})

    const login = () => {
      // const formData = new FormData()
      // const { username,password }  = loginForm.value
      // console.log(username,'---username---')
      // console.log(password,'---password---')
      // formData.append("username", username)
      // formData.append("password", password)
      post("/login",loginForm.value)
        .then(res=>{
          console.log(res,'---login---')
        }).catch(error => {
          console.error(error)
      })
    }
    return {
      loginForm,
      login
    }
  }
})

app.use(ElementPlus,{
  locale: ElementPlusLocaleZhCn,
})
app.use(router)
app.mount('#app')