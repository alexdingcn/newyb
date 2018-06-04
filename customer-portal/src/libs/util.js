import axios from "axios";
import Cookies from "js-cookie";

let util = {};
let env = "development";

const ajaxUrl =
  env === "development"
    ? "http://localhost:8001/customer"
    : "http://erp.yibanjf.com/customer";

util.baseUrl = ajaxUrl;

util.ajax = axios.create({
  baseURL: ajaxUrl,
  timeout: 30000
});
//axios.defaults.withCredentials = true;
util.ajax.defaults.headers["Content-Type"] = "application/json";

// http request 拦截器
util.ajax.interceptors.request.use(
  config => {
    var token = Cookies.get("token");
    var openid = Cookies.get("openid");
    // 判断是否存在token，如果存在的话，则每个http header都加上token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      config.headers.openid = `${openid}`;
    }
    return config;
  },
  err => {
    return Promise.reject(err);
  }
);
/*

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
