package com.yiban.erp.service.mqconsumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import com.yiban.erp.config.RabbitmqQueueConfig;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.service.financial.FinancialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class FinancialSellConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FinancialSellConsumer.class);

    @Autowired
    private RabbitmqQueueConfig rabbitmqQueueConfig;
    @Autowired
    private FinancialService financialService;

    @PostConstruct
    public void initConsumer() {
        try {
            Connection connection = rabbitmqQueueConfig.getConnectionFactory();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(RabbitmqQueueConfig.EXCHANGE, RabbitmqQueueConfig.TOPIC_TYPE, true, false, null);
            channel.queueDeclare(RabbitmqQueueConfig.FINANCIAL_SELL, true, false, false, null);
            /**
             * 参数1：队列名称
             * 参数2：交换器名称
             * 参数3：监听的路由 route key
             */
            channel.queueBind(RabbitmqQueueConfig.FINANCIAL_SELL, RabbitmqQueueConfig.EXCHANGE, RabbitmqQueueConfig.ORDER_SELL);
            channel.basicQos(1); //一次接受一个消息
            //参数1：队列名称, 参数2：禁止自动提交ACK, 参数3: 消费者实现类
            channel.basicConsume(RabbitmqQueueConfig.FINANCIAL_SELL, false, new FinancialSellConsumerProcessor(channel));
        }catch (Exception e) {
            logger.error("init financial sell consumer fail.", e);
        }
    }

    class FinancialSellConsumerProcessor extends DefaultConsumer {
        private Channel channel;
        public FinancialSellConsumerProcessor(Channel channel) {
            super(channel);
            this.channel = channel;
        }
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
            try {
                logger.info("[FinancialSellConsumerProcessor] get an message:{}", JSON.toJSONString(body));
                //根据发送消息的情况，直接解析到销售订单类型
                SellOrder sellOrder = JSON.parseObject(body, SellOrder.class);
                financialService.createFlowBySellOrder(sellOrder);
            }catch (Exception e) {
                logger.error("financial sell order mq message process have exception.", e);
            }finally {
                try {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (IOException e) {
                    logger.error("financial sell order mq message ack fail.", e);
                }
            }
        }
    }


}
