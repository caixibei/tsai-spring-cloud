const instance = axios.create({
	timeout: 1000
})

instance.interceptors.request.use(
	function (config) {
		Pace.start()
		return config
	},
	function (error) {
		Pace.stop()
		return Promise.reject(error)
	}
)

instance.interceptors.response.use(
	function (response) {
		Pace.stop()
		// const res = response.data
		// if (res.code === 401) {
		//     window.location = `${window.logoutUrl}?url=${window.location.origin}`
		// }
		return response
	},
	function (error) {
		Pace.stop()
		return Promise.reject(error)
	}
)

function get(url, params, config) {
	return new Promise(function (resolve, reject) {
		instance.get(url, {
				params: params,
				...config
			})
			.then(function (res) {
				resolve(res.data)
			}).catch(function (err) {
				reject(err.data)
			})
	})
}

function post(url, data, config) {
	return new Promise(function (resolve, reject) {
		instance.post(url, data, config)
			.then(function (res) {
				resolve(res)
			}).catch(function (err) {
				reject(err)
			})
	});
}