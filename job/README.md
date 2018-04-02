
### 医伴 ERP-JOB

> 医伴erp项目的所有定时任务，规划到job工程中，使用的技术是[xxl-job](http://www.xuxueli.com/xxl-job/#/?id=%E3%80%8A%E5%88%86%E5%B8%83%E5%BC%8F%E4%BB%BB%E5%8A%A1%E8%B0%83%E5%BA%A6%E5%B9%B3%E5%8F%B0xxl-job%E3%80%8B). 所有任务定义在job->src->java->com.yiban.erp.executor->jobHandler文件夹下. 使用BEEN方式定义具体的JOB.

> XXL-JOB主要分两种任务执行器，具体定义如下:

#### 简单任务执行器

> 开发例子：example/SimpleJobHandler.java

```java
/**
 * 简单任务（Bean模式）
 *
 * 开发步骤：
 * 1、继承"IJobHandler"：“com.xxl.job.core.handler.IJobHandler”；
 * 2、注册到Spring容器：添加“@Component”注解，被Spring容器扫描为Bean实例；
 * 3、注册到执行器工厂：添加“@JobHandler(value="自定义jobhandler名称")”注解，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 4、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 */
@JobHander(value = "SimpleJobHandler")
@Component
public class SimpleJobHandler extends IJobHandler {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        int result = doWork(strings);
        if (result == 0) {
            return new ReturnT<>("Simple Job SUCCESS");
        }else {
            XxlJobLogger.log("Simple Job result is Fail.");
            return new ReturnT<>(ReturnT.FAIL_CODE, "Simple Job FAIL");
        }
    }

    private int doWork(String[] params) throws Exception {
        XxlJobLogger.log("start an example simple job handler by params: {0}", JSON.toJSONString(params));

        TimeUnit.SECONDS.sleep(10); //test do something job use 10 seconds

        if (params != null) {
            XxlJobLogger.log("begin get user by nickname:{0}", params[0]);
            User user = userMapper.getUser(params[0]);
            XxlJobLogger.log("get user by nicknmae:{0}, result:{1}",
                    params[0], JSON.toJSONString(user));
        }

        int result = RandomUtils.nextInt(0, 10); //从0到10中抽取一个数

        return result < 5 ? 0 : 1; //模拟返回结果
    }

}
```

#### 分配任务执行器

> 开发例子：example/ShardingJobHandler.java

```java
/**
 * 分片广播任务（Bean模式）
 *
 * 开发步骤：
 * 1、继承"IJobHandler"：“com.xxl.job.core.handler.IJobHandler”；
 * 2、注册到Spring容器：添加“@Component”注解，被Spring容器扫描为Bean实例；
 * 3、注册到执行器工厂：添加“@JobHandler(value="自定义jobhandler名称")”注解，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 4、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 */
@JobHander(value = "ShardingJobHandler")
@Component
public class ShardingJobHandler extends IJobHandler{

    @Override
    public ReturnT<String> execute(String... strings) throws Exception {

        //获取分片参数信息
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();

        XxlJobLogger.log("分片参数, 当前分片序号:{0}, 总分片数:{1}",
                shardingVO.getIndex(), shardingVO.getTotal());

        XxlJobLogger.log("sharding job params:{0}", JSON.toJSONString(strings));

        XxlJobLogger.log("begin do sharding demo job business.");
        long startTime = System.currentTimeMillis();
        //业务逻辑
        for (int i=0; i<shardingVO.getTotal(); i++) {
            if (i == shardingVO.getIndex()) {
                XxlJobLogger.log("命中分片{0}，开始处理业务逻辑.", i);
                TimeUnit.SECONDS.sleep(20);
            }else {
                //分片未命中，不做任何处理.
            }
        }

        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("end do sharding demo job business use:{} seconds",
                (endTime - startTime)/1000);
        return ReturnT.SUCCESS;
    }
}
```

#### 注意事项和规范

- 每一个任务对应一个JobHandler，当做每一个job的入口
- 每个JOB任务类继承IJobHandler类，并且注解中的@JobHander的value取值问当前类名
- 日志注意使用XxlJobLogger.log()方法进行打印对应的日志，注意，如果使用参数形式，每一个参数对应的位置值需要表明，否则会报错。如:
```java
// 字符串中的{0},{1}里面的0,1必输，否则如果只是{}这样会报错
XxlJobLogger.log("sharding job params:{0}, {1}, {2}", param0, param1, param2);
```
- 注意返回值，正常使用ReturnT.SUCCESS的结果可以了，如果需要封装一些描述信息，参考ReturnT类的信息,code分两种，code的值要正确.


#### job工程的mapper文件的定义

> XXXMapper.xml文件定义在/resources/mapping下，注意需要在/resources/mybatis-config.xml文件中定义对应的XXXMapper.xml文件. 另外，Mapper.xml文件内容尽量控制在只是需要的才定义，不需要的不用累加了.



### 调度中心

> 调度中心使用xxl-job-admin平台，tomcat部署.

