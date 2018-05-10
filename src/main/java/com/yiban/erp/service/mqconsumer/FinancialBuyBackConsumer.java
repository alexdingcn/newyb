package com.yiban.erp.service.mqconsumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import com.yiban.erp.config.RabbitmqQueueConfig;
import com.yiban.erp.constant.RepertoryInStatus;
import com.yiban.erp.dao.RepertoryInBackMapper;
import com.yiban.erp.entities.RepertoryIn;
import com.yiban.erp.entities.RepertoryInBack;
import com.yiban.erp.service.financial.FinancialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class FinancialBuyBackConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FinancialBuyBackConsumer.class);

    @Autowired
    private RabbitmqQueueConfig rabbitmqQueueConfig;
    @Autowired
    private FinancialService financialService;
    @Autowired
    private RepertoryInBackMapper repertoryInBackMapper;

    @PostConstruct
    public void initConsumer() {
        try {
            Connection connection = rabbitmqQueueConfig.getConnectionFactory();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(RabbitmqQueueConfig.EXCHANGE, RabbitmqQueueConfig.TOPIC_TYPE, true, false, null);
            /**
             * 参数1：队列名称
             * 参数2：true：在RabbitMQ服务器重启时，能够存活,
             * 参数3：exclusive ：是否为当前连接的专用队列，在连接断开后，会自动删除该队列
             * 参数4：autodelete：当没有任何消费者使用时，自动删除该队列
             * 参数5: 参数
             */
            channel.queueDeclare(RabbitmqQueueConfig.FINANCIAL_BUY_BACK, true, false, false, null);
            /**
             * 参数1：队列名称
             * 参数2：交换器名称
             * 参数3：监听的路由 route key
             */
            channel.queueBind(RabbitmqQueueConfig.FINANCIAL_BUY_BACK, RabbitmqQueueConfig.EXCHANGE, RabbitmqQueueConfig.ORDER_BUY_BACK);
            channel.basicQos(1); //一次接受一个消息
            //参数1：队列名称, 参数2：禁止自动提交ACK, 参数3: 消费者实现类
            channel.basicConsume(RabbitmqQueueConfig.FINANCIAL_BUY_BACK, false, new FinancialBuyBackConsumerProcessor(channel));
        }catch (Exception e) {
            logger.error("init financial buy back consumer fail.", e);
        }
    }

    class FinancialBuyBackConsumerProcessor extends DefaultConsumer {
        private Channel channel;
        public FinancialBuyBackConsumerProcessor(Channel channel) {
            super(channel);
            this.channel = channel;
        }
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
            try {
                logger.info("[FinancialBuyBackConsumerProcessor] get an message:{}", JSON.toJSONString(body));
                RepertoryInBack inBack = JSON.parseObject(body, RepertoryInBack.class);
                if (inBack == null || inBack.getId() == null) {
                    logger.error("repertoryInBack order parse result is null.");
                    return;
                }
                RepertoryInBack back = repertoryInBackMapper.getById(inBack.getId()); //包含供应商名称
                financialService.createFlowByBuyBackOrder(back);
            }catch (Exception e) {
                logger.error("financial buy back order mq message process have exception.", e);
            }finally {
                try {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (IOException e) {
                    logger.error("financial buy back order mq message ack fail.", e);
                }
            }
        }
    }


}
