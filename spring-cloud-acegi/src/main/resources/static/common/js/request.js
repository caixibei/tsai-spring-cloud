const instance = axios.create({
  timeout: 1000
});

instance.interceptors.request.use(
  function (config) {
    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

instance.interceptors.response.use(
  function (response) {
    // todo
    // const res = response.data;
    // if (res.code === 401) {
    //     window.location = `${window.logoutUrl}?url=${window.location.origin}`;
    // }
    return response;
  },
  function (error) {
    return Promise.reject(error);
  }
);

function get(url, params) {
  return new Promise((resolve, reject) => {
    instance.get(url, { params: params })
      .then((res) => {
        resolve(res.data);
      })
      .catch((err) => {
        reject(err.data);
      });
  });
}

function post(url, data) {
  return new Promise((resolve, reject) => {
    instance.post(url, data)
      .then((res) => {
        resolve(res);
      })
      .catch((err) => {
        reject(err);
      });
  });
}