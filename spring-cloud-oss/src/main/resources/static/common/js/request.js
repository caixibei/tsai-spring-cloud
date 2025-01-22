// 创建 Axios 请求实例
const instance = axios.create({ timeout: 1000})

// 请求拦截器
instance.interceptors.request.use(config=>config, error => Promise.reject(error))

// 响应拦截器
instance.interceptors.response.use(response =>  response,error => Promise.reject(error))

// get 请求
function get(url, params, config) {
  return new Promise((resolve, reject) => {
    instance.get(url, {
      params: params,
      ...config
    }).then((res) => {
      resolve(res.data)
    }).catch((err) => {
      reject(err.data)
    })
  })
}

// post 请求
function post(url, data, config) {
  return new Promise((resolve, reject) => {
    instance.post(url, data, config).then((res) => {
      resolve(res)
    }).catch((err) => {
      reject(err)
    })
  })
}