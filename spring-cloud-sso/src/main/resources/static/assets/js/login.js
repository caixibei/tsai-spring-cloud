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
        /**表单 ref 实例*/
        const formRef = ref()
        /**表单数据*/
        const loginForm = ref({
            username: 'admin',
            password: '123456',
            captcha: undefined
        })
        /**验证码倒计时信息*/
        const captchaTipMessage = ref()
        /**验证码倒计时 180s */
        const total_seconds = ref(179)
        /**是否显示验证码*/
        const showCaptcha = ref(false)
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
                // if (valid) {
                //   post('/login', Qs.stringify(loginForm.value), {
                //     headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                //   }).then(res => {
                //     console.log(res)
                //     if(res?.data?.code === 500 || res?.data?.code === 400){
                //       ElementPlus.ElMessage({ message: res?.data?.message + res?.data?.details, type: 'error'})
                //     } else {
                //       // 如果授权服务器配置了跳转链接地址，则正确跳转目标页面;
                //       // 反之，如果没有配置跳转链接地址，则跳转统一门户地址；
                //       // const responseURL = res?.request?.responseURL
                //       // const url = new URL(responseURL)
                //       // const params = new URLSearchParams(url.search)
                //       // const targetURL = params.get('target')
                //       // if (!targetURL) {
                //       //   window.location.href = responseURL
                //       // }else{
                //       //   window.location.href = targetURL
                //       // }
                //     }
                //   }).catch(error => {
                //     console.error(error)
                //   })
                // }
                if (valid) {
                    // 使用表单提交而不是 AJAX
                    const form = document.createElement('form')
                    form.method = 'POST'
                    form.action = '/login'
                    form.style.display = 'none'
                    // 添加用户名
                    const usernameInput = document.createElement('input')
                    usernameInput.type = 'hidden'
                    usernameInput.name = 'username'
                    usernameInput.value = loginForm.value.username
                    form.appendChild(usernameInput)
                    // 添加密码
                    const passwordInput = document.createElement('input')
                    passwordInput.type = 'hidden'
                    passwordInput.name = 'password'
                    passwordInput.value = loginForm.value.password
                    form.appendChild(passwordInput)
                    // 添加验证码
                    const captchaInput = document.createElement('input')
                    captchaInput.type = 'hidden'
                    captchaInput.name = 'captcha'
                    captchaInput.value = loginForm.value.captcha
                    form.appendChild(captchaInput)
                    document.body.appendChild(form)
                    form.submit()
                    // 添加提交事件监听器，在表单提交后删除表单元素，防止内存泄露
                    form.addEventListener('submit', function () {
                        setTimeout(() => {
                            if (form.parentNode) {
                                form.parentNode.removeChild(form)
                            }
                        }, 1000)
                    })
                }
            })
        }
        /**获取验证码，校验用户*/
        const getCaptcha = () => {
            if (!loginForm.value?.username) {
                ElementPlus.ElMessage({message: '请输入用户名', type: 'warning'})
                return
            }
            timestamp.value = new Date().getTime()
            username_copy.value = loginForm.value?.username
            showCaptcha.value = true
            total_seconds.value = 179
            const interval = setInterval(() => {
                if (total_seconds.value === 0) {
                    showCaptcha.value = false
                    captchaTipMessage.value = ''
                    clearInterval(interval)
                    return
                }
                captchaTipMessage.value = (total_seconds.value--) + 's'
            }, 1000)
        }
        return {
            formRef,
            loginForm,
            rules,
            timestamp,
            username_copy,
            showCaptcha,
            captchaTipMessage,
            login,
            getCaptcha
        }
    }
})
// 加载 vue-router
app.use(router)
// 加载 Element-Plus，并启用国际化进行汉化
app.use(ElementPlus, {locale: ElementPlusLocaleZhCn})
// 挂载到目标DOM
app.mount('#app')