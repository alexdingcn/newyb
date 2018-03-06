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

一个简单的“上海人员信息查询系统”作为例子

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

- 安装JDK1.7或更新的版本
- 安装Node.js/NPM
- 克隆仓库

        git clone https://github.com/boylegu/SpringBoot-vue.git
        
        cd springboot_vue

### 安装  
        
- 编译前端开发环境

        cd springboot_vue/frontend

        npm install 

### 使用

- 运行Spring Boot后端服务

        cd springboot_vue/target/

        java -jar springboot_vue-0.0.1-SNAPSHOT.jar


[![](https://github.com/boylegu/SpringBoot-vue/blob/master/images/spring_run.png?raw=true)]()

- 运行前端服务

        cd springboot_vue/frontend

        npm run dev


> 你也可以在生产环境中运行`cd springboot_vue/frontend;npm run build`进行编译并配合Nginx
        
## 未来计划

以下功能已经在计划之中：

1. 用户认证
2. 引入更高级的vuex组件通信机制
3. 演示vue-route的使用
4. 加入docker部署环境
5. 新增针对yarn的支持
... ...


