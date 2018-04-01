package com.yiban.erp.executor.jobHandler.example;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.log.XxlJobLogger;
import com.yiban.erp.executor.dao.UserMapper;
import com.yiban.erp.executor.entities.User;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


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
