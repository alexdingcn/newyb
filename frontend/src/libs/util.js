import axios from "axios";
import env from "../../build/env";
import semver from "semver";
import packjson from "../../package.json";
import { stringify } from "querystring";
import store from "../store";
import router from "../router";

let util = {};
util.title = function(title) {
  title = title || "医伴金服ERP";
  window.document.title = title;
};

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

util.inOf = function(arr, targetArr) {
  let res = true;
  arr.forEach(item => {
    if (targetArr.indexOf(item) < 0) {
      res = false;
    }
  });
  return res;
};

util.oneOf = function(ele, targetArr) {
  if (targetArr.indexOf(ele) >= 0) {
    return true;
  } else {
    return false;
  }
};

util.showThisRoute = function(routeName, currAccessRoutes) {
  if (currAccessRoutes && Array.isArray(currAccessRoutes)) {
    return util.oneOf(routeName, currAccessRoutes);
  } else {
    return false;
  }
};

util.getRouterObjByName = function(routers, name) {
  if (!name || !routers || !routers.length) {
    return null;
  }
  // debugger;
  let routerObj = null;
  for (let item of routers) {
    if (item.name === name) {
      return item;
    }
    routerObj = util.getRouterObjByName(item.children, name);
    if (routerObj) {
      return routerObj;
    }
  }
  return null;
};

util.handleTitle = function(vm, item) {
  if (typeof item.title === "object") {
    return vm.$t(item.title.i18n);
  } else {
    return item.title;
  }
};

util.setCurrentPageTitle = function(vm, title, isSub) {
  var openedPage = vm.$store.state.app.pageOpenedList.filter(
    page => page.name === vm.$store.state.app.currentPageName
  );
  if (openedPage && openedPage.length > 0) {
    if (!isSub) {
      openedPage[0].title = title;
    } else {
      var oldTitle = openedPage[0].title;
      var lastIndex = oldTitle.lastIndexOf("-");
      if (lastIndex < 0) {
        openedPage[0].title = oldTitle + " - " + title;
      } else if (title !== "") {
        openedPage[0].title = oldTitle.substr(0, lastIndex + 1) + " " + title;
      }
    }
  }
};

util.setCurrentPath = function(vm, name) {
  let title = "";
  let isOtherRouter = false;
  vm.$store.state.app.routers.forEach(item => {
    if (item.children.length === 1) {
      if (item.children[0].name === name) {
        title = util.handleTitle(vm, item);
        if (item.name === "otherRouter") {
          isOtherRouter = true;
        }
      }
    } else {
      item.children.forEach(child => {
        if (child.name === name) {
          title = util.handleTitle(vm, child);
          if (item.name === "otherRouter") {
            isOtherRouter = true;
          }
        }
      });
    }
  });
  let currentPathArr = [];
  if (name === "home_index") {
    currentPathArr = [
      {
        title: util.handleTitle(
          vm,
          util.getRouterObjByName(vm.$store.state.app.routers, "home_index")
        ),
        path: "",
        name: "home_index"
      }
    ];
  } else if (
    (name.indexOf("_index") >= 0 || isOtherRouter) &&
    name !== "home_index"
  ) {
    currentPathArr = [
      {
        title: util.handleTitle(
          vm,
          util.getRouterObjByName(vm.$store.state.app.routers, "home_index")
        ),
        path: "/home",
        name: "home_index"
      },
      {
        title: title,
        path: "",
        name: name
      }
    ];
  } else {
    let currentPathObj = vm.$store.state.app.routers.filter(item => {
      if (item.children.length <= 1) {
        return item.children[0].name === name;
      } else {
        let i = 0;
        let childArr = item.children;
        let len = childArr.length;
        while (i < len) {
          if (childArr[i].name === name) {
            return true;
          }
          i++;
        }
        return false;
      }
    })[0];
    if (currentPathObj.children.length <= 1 && currentPathObj.name === "home") {
      currentPathArr = [
        {
          title: "首页",
          path: "",
          name: "home_index"
        }
      ];
    } else if (
      currentPathObj.children.length <= 1 &&
      currentPathObj.name !== "home"
    ) {
      currentPathArr = [
        {
          title: "首页",
          path: "/home",
          name: "home_index"
        },
        {
          title: currentPathObj.title,
          path: "",
          name: name
        }
      ];
    } else {
      let childObj = currentPathObj.children.filter(child => {
        return child.name === name;
      })[0];
      currentPathArr = [
        {
          title: "首页",
          path: "/home",
          name: "home_index"
        },
        {
          title: currentPathObj.title,
          path: "",
          name: currentPathObj.name
        },
        {
          title: childObj.title,
          path: currentPathObj.path + "/" + childObj.path,
          name: name
        }
      ];
    }
  }
  vm.$store.commit("setCurrentPath", currentPathArr);

  return currentPathArr;
};

util.openNewPage = function(vm, name, argu, query) {
  let pageOpenedList = vm.$store.state.app.pageOpenedList;
  let openedPageLen = pageOpenedList.length;
  let i = 0;
  let tagHasOpened = false;
  while (i < openedPageLen) {
    if (name === pageOpenedList[i].name) {
      // 页面已经打开
      vm.$store.commit("pageOpenedList", {
        index: i,
        argu: argu,
        query: query
      });
      tagHasOpened = true;
      break;
    }
    i++;
  }
  if (!tagHasOpened) {
    let tag = vm.$store.state.app.tagsList.filter(item => {
      if (item.children) {
        return name === item.children[0].name;
      } else {
        return name === item.name;
      }
    });
    tag = tag[0];
    if (tag) {
      tag = tag.children ? tag.children[0] : tag;
      if (argu) {
        tag.argu = argu;
      }
      if (query) {
        tag.query = query;
      }
      vm.$store.commit("increateTag", tag);
    }
  }
  vm.$store.commit("setCurrentPageName", name);
};

util.closeCurrentTab = function(vm) {
  let pageOpenedList = vm.$store.state.app.pageOpenedList;
  let name = vm.$store.state.app.currentPageName;
  vm.$store.commit("removeTag", name);
  vm.$store.commit("closePage", name);
  pageOpenedList = vm.$store.state.app.pageOpenedList;
  localStorage.pageOpenedList = JSON.stringify(pageOpenedList);
  let pageName = pageOpenedList[pageOpenedList.length - 1].name;
  vm.$store.commit("setCurrentPageName", pageName);
  return pageName;
};

util.toDefaultPage = function(routers, name, route, next) {
  let len = routers.length;
  let i = 0;
  let notHandle = true;
  while (i < len) {
    if (
      routers[i].name === name &&
      routers[i].children &&
      routers[i].redirect === undefined
    ) {
      route.replace({
        name: routers[i].children[0].name
      });
      notHandle = false;
      next();
      break;
    }
    i++;
  }
  if (notHandle) {
    next();
  }
};

util.fullscreenEvent = function(vm) {
  vm.$store.commit("initCachepage");
  // 权限菜单过滤相关
  vm.$store.commit("updateMenulist", vm.$store.state.user.accessRoutes);
  // 全屏相关
};

util.checkUpdate = function(vm) {
  axios
    .get("https://api.github.com/repos/iview/iview-admin/releases/latest")
    .then(res => {
      let version = res.data.tag_name;
      vm.$Notice.config({
        duration: 0
      });
      if (semver.lt(packjson.version, version)) {
        vm.$Notice.info({
          title: "iview-admin更新啦",
          desc:
            "<p>iView-admin更新到了" +
            version +
            '了，去看看有哪些变化吧</p><a style="font-size:13px;" href="https://github.com/iview/iview-admin/releases" target="_blank">前往github查看</a>'
        });
      }
    });
};

function showErrorMessage(vm, data) {
  if (!data) {
    vm.$Notice.error({
      title: "系统异常",
      desc: "未收到返回数据, 请联系客服"
    });
  }
  let display = data.display ? data.display : 1;
  let code = data.code ? data.code : 9999;
  let message = data.message ? data.message : "服务器连接中断";
  switch (display) {
    case 1:
      // vm.$Message.error(message);
      // break;
      vm.$Notice.error({
        title: "错误码: " + code,
        desc: message
      });
      break;
    case 2:
      vm.$Notice.error({
        title: "错误码: " + code,
        desc: message
      });
      break;
    case 3:
      vm.$Modal.error({
        title: "错误码: " + code,
        content: message
      });
      break;
    default:
      vm.$Notice.error({
        title: "系统异常",
        desc: "系统数据错误, 请联系运营人员"
      });
      break;
  }
}

util.errorProcessor = function(vm, error, callback) {
  let response = error.response;
  if (response) {
    let httpCode = response.status;
    let data = response.data;
    if (httpCode === 401) {
      vm.$Message.warning("登录超时, 请重新登录");
    } else if (httpCode === 403) {
      vm.$router.push("error-403", { params: data });
    } else if (httpCode === 404) {
      vm.$Notice.error({
        title: "系统异常",
        desc: "获取系统资源路径失败, 请联系技术人员"
      });
    } else {
      if (callback) {
        callback(data);
      } else {
        showErrorMessage(vm, data);
      }
    }
  } else {
    showErrorMessage(vm, "服务器连接中断");
  }
};

export default util;
