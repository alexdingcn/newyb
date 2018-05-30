import axios from "axios";

let util = {};
let env = "development";

const ajaxUrl =
  env === "development"
    ? "http://localhost:8000"
    : env === "production"
      ? "http://erp.yibanjf.com"
      : "https://debug.yibanjf.com";

util.baseUrl = ajaxUrl;

util.ajax = axios.create({
  baseURL: ajaxUrl,
  timeout: 30000
});

util.ajax.defaults.headers["Content-Type"] = "application/json";

/*
// http request 拦截器
util.ajax.interceptors.request.use(
    config => {
        // 判断是否存在token，如果存在的话，则每个http header都加上token
        if (store.state.user.token) {
            config.headers.Authorization = `Bearer ${store.state.user.token}`;
        }
        return config;
    },
    err => {
        return Promise.reject(err);
    }
);

// http response 拦截器
util.ajax.interceptors.response.use(
    response => {
        return response;
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 返回 401 清除token信息并跳转到登录页面
                    store.commit("logout", this);

                    router.replace({
                        path: "/login",
                        query: { redirect: router.currentRoute.fullPath }
                    });
            }
        }
        return Promise.reject(error); // 返回接口返回的错误信息
    }
);
*/
export default util;
