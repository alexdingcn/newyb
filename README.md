## YibanERP

SpringBoot ＋ 前端MVVM Vue.js 基于Java的微服务全栈快速开发实践


### 后端API代码

~~~~java
@RestController
@RequestMapping("/api/persons")
public class MainController {

    @RequestMapping(
            value = "/detail/{id}", 
            method = RequestMethod.GET, 
            produces = MediaType.APPLICATION_JSON_VALUE
            )
    public ResponseEntity<Persons> getUserDetail(@PathVariable Long id) {

        /*
        *    @api {GET} /api/persons/detail/:id  details info
        *    @apiName GetPersonDetails
        *    @apiGroup Info Manage
        *    @apiVersion 1.0.0
        *
        *    @apiExample {httpie} Example usage:
        *
        *        http GET http://127.0.0.1:8000/api/persons/detail/1
        *
        *    @apiSuccess {String} email
        *    @apiSuccess {String} id
        *    @apiSuccess {String} phone
        *    @apiSuccess {String} sex
        *    @apiSuccess {String} username
        *    @apiSuccess {String} zone
        */

        Persons user = personsRepository.findById(id);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
~~~~

### 为什么选择Vue.js

Vue.js是MVVM设计模式中目前最火热的一个前端框架之一，除了性能表现优异之外，与类似React相比，更轻量级、更容易上手。

通过Vue中的“单文件组件”特性，更灵活的定义组件，不仅使代码结构更清晰，而且能与任何其他组件进行随意组合，更具复用性。


### Webpack是什么
Webpack提供了一整套前端工程自动化的解决方案

## Demo

一个简单的“人员信息查询系统”作为例子

[![demo-image](https://github.com/boylegu/SpringBoot-vue/blob/master/images/demo.gif?raw=true)]()

### 具备的功能(v0.1)

- Spring Boot (后端)
  
  - 通过在Spring Boot中建立基于RestFul-API并使用`@ RequestMapping`实现一个基本的CRUD逻辑
  
  - 处理CORS(跨域资源共享)
  
  - 在Spring Boot中进行单元测试

  - 支持热加载
  
  - 增加api接口文档 
  
  - 通过SpringBoot配合JPA来实现RestFul-API的分页
   
- VueJS & webpack (前端)

  - 遵循ECMAScript 6 规范
  
  - 如何在vue中使用‘单文件组件’进行开发编码
  
  - 演示‘非父子组件’如何进行简单的通信以及‘父子组件’之间如何传递数据
  
  - 如何和后端进行数据交互

  - 如何在vue中优雅的引入第三方JS库

  - 格式化时间
  
  - 分页实现
  
  - 可复用组件
 
     - DbHeader.vue
     - DbFooter.vue  (sticky footer) 
     - DbFilterinput.vue
     - DbModal.vue
     - DbSidebar.vue
     - DbTable.vue 
     
     >> 得益于类似vue、react等MVVM模式，本项目的任何组件，只要您觉得合适，都可以复用在您的任何项目中，避免重复造轮子。
  
  - 如何通过webpack2配置来自动化构建前端环境(包括如何配置vue2、处理静态文件,构建不同环境等等)  

### 本项目主要技术栈
- Java 1.7
- Spring Boot 1.5.x
- Maven
- sqlite (not recommend, only convenience example)
- vueJS 2.x
- webpack 2.x
- element ui
- axios

### 准备工作

- 安装JDK1.8或更新的版本
- 安装Node.js/NPM
- 克隆仓库

        git clone https://github.com/alexdingcn/newyb
        
        cd newyb

### 安装  
        
- 编译前端开发环境

        cd newyb/frontend

        npm install 

### 使用

- 运行Spring Boot后端服务

        cd newyb/target/

        java -jar erp-1.1.1-SNAPSHOT.jar


[![](https://github.com/boylegu/SpringBoot-vue/blob/master/images/spring_run.png?raw=true)]()

- 运行前端服务

        cd newyb/frontend

        npm run dev


> 你也可以在生产环境中运行`cd newyb/frontend;npm run build`进行编译并配合Nginx

### 从建表到前端

- MySql建表

        要包含
        id(自增）
        created_time(timestamp)
        created_by(varchar(32))
        updated_time(timestamp)
        updated_by(varchar(32))
        
- 修改generatorConfig.xml, 用plugin生成Mapper

		 mvn mybatis-generator:generate

- 修改Mapper.xml，更改id生成方式, 添加useGeneratedKeys="true" keyProperty="id"

		<insert id="insertSelective" parameterType="com.yiban.erp.entities.BizWxApply" useGeneratedKeys="true" keyProperty="id">
		 
- 修改*Mapper.java文件，增加@Mapper的标注
- 修改mybatis-config.xml，增加新的mapper文件
- 创建@RestController，增加CRUD操作
- 前端通过util.ajax.get或者util.ajax.post调用后端服务
- 如果api不需要登录保护，修改WebSecurityConfig，添加到ignore列表

		web.ignoring().antMatchers("/register", "/loan/**", "/phone/**");
		 


### 后台数据接口封装

- 错误异常处理方式

> 所有的错误使用异常形式抛出, 所有的异常处理使用捕获异常统一处理(见GlobalExceptionHandler), 所有的controller层的方法全部把异常抛出。如下:

````
    @RequestMapping(value = "/xxx", method = RequestMethod.POST) 
    public ResponseEntity<String> doSomething() throws Exception {
        // do something
        return ResponseEntity.ok().body("xxx");
    }
````

- 错误码定义

> 错误码定义在ErrorCode 中使用enum定义关键key名称和code, 具体错误信息描述和显示方案，定义在数据库error_message 表中。
> 改数据表数据在系统启动时，自动加载入本地缓存，使用Map的数据形式，根据code获取对应的错误描述信息。

- 错误异常数据接口封装

````JSON
    {
        code: 9999,
        message: "错误信息",
        display: 1,
        timestamp: 123456789
        url: "https://xxxx.com/xxxx",
        data: {}
    }
````

- 格式描述


|字段|类型|描述|
|-|-|-|
|code|Integer|错误码|
|message|String|错误提示信息|
|display|Integer|展示方式(iview的三种提示方式): <br>1-Message()<br/>2-Notice<br/>3-Modal|
|timestamp|Data|报错时间|
|url|String|请求资源路径|
|data|Object|补充数据|


- display展示方式

1. Message

[![](https://github.com/alexdingcn/newyb/raw/master/images/display-Message.png?raw=true)]()

> iview 中的定义的全局Message提示框, 使用 ````this.$Message.error(message)```` 方式直接提示

2. Notice 

[![](https://github.com/alexdingcn/newyb/raw/master/images/display-Notice.png?raw=true)]()

> iview 中定义的Notice通知提醒, 使用````this.$Notice.error({title:'错误码：' + code, desc: message})```` 方式直接提示, 可以根据情景自定义显示内容

3. Modal 

[![](https://github.com/alexdingcn/newyb/raw/master/images/display-Modal.png?raw=true)]()

> iview 中定义的Modal对话框, 使用````this.$Modal.error({title:'错误码：' + code, content: message})```` 方式直接提示, 可以根据情景自定义显示内容


### 前端ajax错误统一处理

> 方式如下代码，在有回调函数的情况下，直接返回的是后台定义的错误异常数据对象，如果没有定义有回调函数，直接按错误信息中定义的展示方式展示错误信息

````js
function showErrorMessage (vm, data) {
    if (!data) {
        vm.$Notice.error({
            title: '系统异常',
            desc: '系统数据格式错误, 请联系技术人员'
        });
    }
    let display = data.display;
    let code = data.code ? data.code : 9999;
    let message = data.message ? data.message : '交易出现异常';
    switch (display) {
        case 1:
            vm.$Message.error(message);
            break;
        case 2:
            vm.$Notice.error({
                title: '错误码: ' + code,
                desc: message
            });
            break;
        case 3:
            vm.$Modal.error({
                title: '错误码: ' + code,
                content: message
            });
            break;
        default:
            vm.$Notice.error({
                title: '系统异常',
                desc: '系统数据格式错误, 请联系技术人员'
            });
            break;
    }
};

util.errorProcessor = function (vm, error, callback) {
    let httpCode = error.status;
    let data = error.data;
    if (httpCode === 403) {
        vm.$router.push('error-403', { params: data });
    } else if (httpCode === 500) {
        vm.$router.push('error-500', { params: data });
    } else {
        if (callback) {
            callback(data);
        } else {
            showErrorMessage(vm, data);
        }
    }
};

````

## 代码规范

- 后端(JAVA)

1. 类名：使用大写字母开头，驼峰式命名，如：BuyOrder.class
2. 属性：类的属性使用小写字母开头，驼峰式命名, 如：private Long orderId;
3. 枚举类型：使用全大写字母，单词间使用下划线_链接, 如：IN_CHECKED;
4. 常量：注意使用static final 进行修饰的字符串常量，使用全大写, 下划线链接, 如：
public static final String XXX_XXX = 'xxxx';
5. 方法名：使用小写字母开头，驼峰式命名，尽量表达方法的功能，如果是获取数据，使用getXxx的开头形式,修改数据类型操作，尽量使用:updateXxx或者setXxx的形式.
6. 数据库表名,使用全小写字母，单词间使用_下划线分割，表字段名也全使用小写，下划线链接.
7. 注意，修改了数据库字段(添加、删除、修改), 都需要维护号对应的Mapper.xml文件的内容，保持与表字段能一一对应
8. 与数据库交换数据的实体类，保存在entities文件夹中, 与前端交互使用到一些额外的对象类，保存在dto文件夹中.
9. controller对应前端的入口，类的注解注意统一路径问题,统一路径在类上注解，分点功能在方法上注解.
10. controller中尽量不处理业务逻辑, 在service中处理对应的业务逻辑.
11. 统一错误处理逻辑方案，参考上面错误处理逻辑。
12. 工具类放在util文件夹下.
13. 常量定义放在constant文件夹下.

- 前端

1. 文件名定义：全小写,使用减号(-)链接.
2. 对应功能尽量放在同一个对应功能文件夹下
3. 尽量符合eslintrc的代码规范.

        
## 未来计划

以下功能已经在计划之中：

1. 用户认证
2. 引入更高级的vuex组件通信机制
3. 演示vue-route的使用
4. 加入docker部署环境
5. 新增针对yarn的支持
... ...


