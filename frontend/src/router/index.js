import Vue from 'vue';
import iView from 'iview';
import Util from '../libs/util';
import VueRouter from 'vue-router';
import Cookies from 'js-cookie';
import store from '../store';
import {routers, otherRouter, appRouter} from './router';

Vue.use(VueRouter);

// 页面刷新时，重新赋值token
if (window.localStorage.getItem('token')) {
    store.commit('setToken', window.localStorage.getItem('token'));
};
if (window.localStorage.getItem('userDetail')) {
    store.commit('setUserDetail', window.localStorage.getItem('userDetail'));
};
if (window.localStorage.getItem('accessRoutes')) {
    store.commit('setAccessRoutes', window.localStorage.getItem('accessRoutes'));
};

// 路由配置
const RouterConfig = {
    // mode: 'history',
    routes: routers
};

export const router = new VueRouter(RouterConfig);

router.beforeEach((to, from, next) => {
    iView.LoadingBar.start();
    Util.title(to.meta.title);

    if (!to.meta.noAuth) { // 判断该路由是否需要登录权限
        if (store.state.user && store.state.user.token) { // 通过vuex state获取当前的token是否存在
            next();
        } else {
            next({
                name: 'login'
            });
        }
    } else {
        next();
    }
});

router.afterEach((to) => {
    Util.openNewPage(router.app, to.name, to.params, to.query);
    iView.LoadingBar.finish();
    window.scrollTo(0, 0);
});

export default router;
