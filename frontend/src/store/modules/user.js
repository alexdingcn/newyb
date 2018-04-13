import Cookies from 'js-cookie';

const user = {
    state: {
        token: null,
        userDetail: null,
        accessRoutes: null
    },
    mutations: {
        setToken (state, data) {
            localStorage.token = data;
            state.token = data;
        },
        setUserDetail (state, data) {
            if (!data) {
                localStorage.userDetail = null;
                state.userDetail = null;
            } else {
                if (typeof data === 'string') {
                    let result = JSON.parse(data);
                    localStorage.userDetail = JSON.stringify(result);
                    state.userDetail = result;
                } else {
                    localStorage.userDetail = JSON.stringify(data);
                    state.userDetail = data;
                }
            }
        },
        setAccessRoutes (state, data) {
            if (data) {
                if (Array.isArray(data)) {
                    localStorage.accessRoutes = JSON.stringify(data);
                    state.accessRoutes = data;
                } else {
                    let routes = JSON.parse(data);
                    localStorage.accessRoutes = JSON.stringify(routes);
                    state.accessRoutes = routes;
                }
            } else {
                localStorage.accessRoutes = null;
                state.accessRoutes = null;
            }
        },
        logout (state, vm) {
            // Cookies.remove('user');
            Cookies.remove('token');
            localStorage.token = null;
            state.token = null;
            state.userDetail = null;
            localStorage.userDetail = null;
            state.accessRoutes = null;
            localStorage.accessRoutes = null;
            // 恢复默认样式
            let themeLink = document.querySelector('link[name="theme"]');
            themeLink.setAttribute('href', '');
            // 清空打开的页面等数据，但是保存主题数据
            let theme = '';
            if (localStorage.theme) {
                theme = localStorage.theme;
            }
            localStorage.clear();
            if (theme) {
                localStorage.theme = theme;
            }
        }
    }
};

export default user;
