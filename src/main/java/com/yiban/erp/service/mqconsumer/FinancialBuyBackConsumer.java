package com.yiban.erp.service.mqconsumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import com.yiban.erp.config.RabbitmqQueueConfig;
import com.yiban.erp.entities.RepertoryIn;
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

    @PostConstruct
    public void initConsumer() {
        try {
            Connection connection = rabbitmqQueueConfig.getConnectionFactory();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(RabbitmqQueueConfig.EXCHANGE, RabbitmqQueueConfig.TOPIC_TYPE, true, false, null);
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
                // TODO 采购退货财务逻辑

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
