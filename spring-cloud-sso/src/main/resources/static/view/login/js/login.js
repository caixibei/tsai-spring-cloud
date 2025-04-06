const createApp = Vue.createApp;
const ref = Vue.ref;
const reactive = Vue.reactive;
const computed = Vue.computed;
const onMounted = Vue.onMounted;
const defineEmits = Vue.defineEmits;
const defineProps = Vue.defineProps;
const markRaw = Vue.markRaw;
const animate = anime.animate;

const app = createApp({
    setup: function () {
        onMounted(() => {
            titleAnimation()
            formAnimation()
            titleSpanAnimation()
            formAreaAnimation()
        })

        /** 表单 ref 实例 */
        const formRef = ref()
        /** 表单数据 */
        const loginForm = ref({
            username: 'admin',
            password: '123456',
            captcha: undefined
        })
        /** 验证码倒计时信息 */
        const interval = ref()
        const captchaTipMessage = ref()
        /** 验证码倒计时 180s */
        const total_seconds = ref(179)
        /** 是否显示验证码 */
        const showCaptcha = ref(false)
        /** 表单校验规则 */
        const rules = ref({
            username: [{
                required: true,
                trigger: 'blur',
                message: '用户名不可为空'
            }],
            password: [{
                required: true,
                trigger: 'blur',
                message: '密码不可为空'
            }],
            captcha: [{
                required: true,
                trigger: 'blur',
                message: '验证码不可为空'
            }],
        })
        /** 时间戳 */
        const timestamp = ref(0)
        const username_copy = ref('')
        /** 登录逻辑 */
        const login = () => {
            formRef.value.validate((valid) => {
                if (valid) {
                    // 使用表单提交而不是 Axios ，会导致跳转逻辑不对
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
        /** 获取验证码，校验用户 */
        const getCaptcha = () => {
            if (!loginForm.value && loginForm.value.username) {
                ElementPlus.ElMessage({
                    message: '请输入用户名',
                    type: 'warning'
                })
                return
            }
            timestamp.value = new Date().getTime()
            username_copy.value = loginForm.value && loginForm.value.username
            showCaptcha.value = true
            total_seconds.value = 179
            interval.value && clearInterval(interval.value)
            interval.value = setInterval(() => {
                if (total_seconds.value === 0) {
                    showCaptcha.value = false
                    captchaTipMessage.value = ''
                    interval.value && clearInterval(interval.value)
                    return
                }
                captchaTipMessage.value = (total_seconds.value--) + 's'
            }, 1000)
        }

        const titleSpanAnimation = () => {
            animate('.login_title span', {
                y: [{
                        to: '-2.75rem',
                        ease: 'outExpo',
                        duration: 600
                    },
                    {
                        to: 0,
                        ease: 'outBounce',
                        duration: 800,
                        delay: 100
                    }
                ],
                rotate: {
                    from: '-1turn',
                    delay: 0
                },
                delay: (_, i) => i * 50,
                ease: 'inOutCirc',
                loopDelay: 1000,
                loop: true
            });
        }

        const formAnimation = () => {
            animate('.el-form-item', {
                translateX: [-30, 0],
                opacity: [0, 1],
                duration: 800,
                delay: anime.stagger(100),
                easing: 'easeOutQuad'
            });
        }

        const formAreaAnimation = () => {
            animate('.login_area', {
                scale: [1, 1.1],
                duration: 1500,
                direction: 'alternate',
                loop: true,
                easing: 'easeInOutQuad'
            });
            animate('.login_area', {
                duration: 2000,
                loop: true,
                direction: 'alternate',
                easing: 'easeInOutSine',
            });
        }

        const titleAnimation = () => {
            animate('.login_title', {
                translateY: [-50, 0],
                opacity: [0, 1],
                duration: 1200,
                easing: 'spring(1, 80, 10, 0)'
            });
        }

        return {
            formRef: formRef,
            loginForm: loginForm,
            rules: rules,
            timestamp: timestamp,
            username_copy: username_copy,
            showCaptcha: showCaptcha,
            captchaTipMessage: captchaTipMessage,
            login: login,
            getCaptcha: getCaptcha
        }
    }
})

app.use(ElementPlus, {
    locale: ElementPlusLocaleZhCn,
});
app.mount("#app");