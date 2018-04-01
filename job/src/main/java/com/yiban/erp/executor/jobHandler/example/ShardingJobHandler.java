package com.yiban.erp.executor.jobHandler.example;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


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
