package com.yiban.erp.service.mqconsumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import com.yiban.erp.config.RabbitmqQueueConfig;
import com.yiban.erp.dao.SellOrderBackMapper;
import com.yiban.erp.entities.SellOrderBack;
import com.yiban.erp.service.financial.FinancialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class FinancialSellBackConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FinancialSellBackConsumer.class);

    @Autowired
    private RabbitmqQueueConfig rabbitmqQueueConfig;
    @Autowired
    private FinancialService financialService;
    @Autowired
    private SellOrderBackMapper sellOrderBackMapper;

    @PostConstruct
    public void initConsumer() {
        try {
            Connection connection = rabbitmqQueueConfig.getConnectionFactory();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(RabbitmqQueueConfig.EXCHANGE, RabbitmqQueueConfig.TOPIC_TYPE, true, false, null);
            channel.queueDeclare(RabbitmqQueueConfig.FINANCIAL_SELL_BACK, true, false, false, null);
            /**
             * 参数1：队列名称
             * 参数2：交换器名称
             * 参数3：监听的路由 route key
             */
            channel.queueBind(RabbitmqQueueConfig.FINANCIAL_SELL_BACK, RabbitmqQueueConfig.EXCHANGE, RabbitmqQueueConfig.ORDER_SELL_BACK);
            channel.basicQos(1); //一次接受一个消息
            //参数1：队列名称, 参数2：禁止自动提交ACK, 参数3: 消费者实现类
            channel.basicConsume(RabbitmqQueueConfig.FINANCIAL_SELL_BACK, false, new FinancialSellBackConsumerProcessor(channel));
        }catch (Exception e) {
            logger.error("init financial sell back consumer fail.", e);
        }
    }

    class FinancialSellBackConsumerProcessor extends DefaultConsumer {
        private Channel channel;
        public FinancialSellBackConsumerProcessor(Channel channel) {
            super(channel);
            this.channel = channel;
        }
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
            try {
                logger.info("[FinancialSellBackConsumerProcessor] get an message:{}", JSON.toJSONString(body));
                SellOrderBack back = JSON.parseObject(body, SellOrderBack.class);
                if(back == null) {
                    logger.warn("SellOrderBack back order parse fail.");
                    return;
                }
                SellOrderBack orderBack = sellOrderBackMapper.selectByPrimaryKey(back.getId());
                financialService.createFlowBySellBackOrder(orderBack);
            }catch (Exception e) {
                logger.error("financial sell back order mq message process have exception.", e);
            }finally {
                try {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (IOException e) {
                    logger.error("financial sell back order mq message ack fail.", e);
                }
            }
        }
    }
}
