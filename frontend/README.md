## 医伴 ERP 前端开发注意事项

#### 技术栈

* 使用技术[VUE](https://cn.vuejs.org/v2/guide/index.html), 主要使用[IVIEW 组件库](https://www.iviewui.com/docs/guide/start)进行开发。
* axios HTTP 请求
* [moment](http://momentjs.cn/docs/)时间管理工具对时间进行格式化
* vue-router 页面跳转
* 页面基础路径使用'@'符号设置为/src 根路径

#### axios HTTP 请求方式

> [util.js](./src/libs/util.js)对 axios 请求进行封装,统一请求头内容，HTTP 请求响应格式使用 JSON 数据格式, 固定了响应结果的异常处理方案，如下：

```js
import util from "@/libs/util.js";

doGetActionExample() {
    util.ajax.get('/xxx/xxx', {params: {name: value}})
        .then((response) => {
            //HTTP request success.
            //HTTP STATUS 200
            //注意: response.data 才是后台返回的内容信息
            let responseData = response.data;
        })
        .catch((error) => {
            //统一错误处理
            util.errorProcessor(this, error);
        })
}

doPostActionExample() {
    let requestParam = {
        key1: value1,
        key2: value2
    };
    util.ajax.get('/xxx/xxx', requestParam)
        .then((response) => {
            //HTTP request success.
            //HTTP STATUS 200
            //注意: response.data 才是后台返回的内容信息
            let responseData = response.data;
        })
        .catch((error) => {
            //统一错误处理
            util.errorProcessor(this, error);
        })
}

doDeleteActionExample() {
    util.ajax.delete('/xxx/xxx/'+ removeId)
        .then((response) => {
            //HTTP request success.
            //HTTP STATUS 200
            //注意: response.data 才是后台返回的内容信息
            let responseData = response.data;
        })
        .catch((error) => {
            //统一错误处理
            util.errorProcessor(this, error);
        })
}
```

#### 统一错误信息描述

> 在后台已经对所有的错误已经进行统一的数据格式封装，具体封装格式如下：

```JSON
{
    code: 错误码(Integer),
    message: 错误信息描述(String),
    display: 显示方式(Integer),
    timestamp: 错误时间戳(Date)
    url: 请求路径,
    data: 附加内容(JSON格式)
}
```

> 前端的统一错误处理在 util.errorProcess(vm, error[, callback])方法中封装, 主要参数有三个，前两个必输，第一个问请求的页面 this 参数, error 为 ajax 异常是 catch 到的 error 信息, callback 是回调方法, 回调方法可以不存在，如果存在，则不会进行统一处理逻辑，直接返回 error 中的 error.response.data 的数据, 由回调函数进行错误处理. error 的数据格式, 参考 axios 的请求错误格式.

#### 错误等级封装

> 后台在错误异常结果返回的数据格式中，都封装了 display(显示方式)的封装, display 的值有以下三种

* 1: Message(iview 组件中的$Message 组件显示错误信息), 提示格式如下图:

[![](https://github.com/alexdingcn/newyb/raw/master/images/display-Message.png?raw=true)]()

* 2：Notice(iview 组件中的$Notice 组件提示错误信息), 提示如下:

[![](https://github.com/alexdingcn/newyb/raw/master/images/display-Notice.png?raw=true)]()

* 3: Modal(iview 组件中的$Modal 组件弹出框提示), 提示如下:

[![](https://github.com/alexdingcn/newyb/raw/master/images/display-Modal.png?raw=true)]()

#### 系统文件和路径命名

> 按功能菜单区分对应的功能的文件路径，同一功能类型放在对应的文件夹下 例如: 采购功能涉及的一些功能, 如下路径:

```shell
.
├── build  项目构建配置
└── src
    ├── images  图片文件
    ├── libs  工具方法
    |──views
        ├── buy  采购功能菜单
        |   ├── buy-order.vue  采购制单
        |   ├── buy-order-list.vue  采购审核
        ├── selector  常用选择器组件
```
