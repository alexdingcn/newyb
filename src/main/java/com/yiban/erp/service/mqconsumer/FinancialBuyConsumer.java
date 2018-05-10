package com.yiban.erp.service.mqconsumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import com.yiban.erp.config.RabbitmqQueueConfig;
import com.yiban.erp.dao.RepertoryInMapper;
import com.yiban.erp.entities.RepertoryIn;
import com.yiban.erp.service.financial.FinancialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class FinancialBuyConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FinancialBuyConsumer.class);

    @Autowired
    private RabbitmqQueueConfig rabbitmqQueueConfig;
    @Autowired
    private FinancialService financialService;
    @Autowired
    private RepertoryInMapper repertoryInMapper;

    @PostConstruct
    public void initConsumer() {
        try {
            Connection connection = rabbitmqQueueConfig.getConnectionFactory();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(RabbitmqQueueConfig.EXCHANGE, RabbitmqQueueConfig.TOPIC_TYPE, true, false, null);
            channel.queueDeclare(RabbitmqQueueConfig.FINANCIAL_BUY, true, false, false, null);
            /**
             * 参数1：队列名称
             * 参数2：交换器名称
             * 参数3：监听的路由 route key
             */
            channel.queueBind(RabbitmqQueueConfig.FINANCIAL_BUY, RabbitmqQueueConfig.EXCHANGE, RabbitmqQueueConfig.ORDER_BUY);
            channel.basicQos(1); //一次接受一个消息
            //参数1：队列名称, 参数2：禁止自动提交ACK, 参数3: 消费者实现类
            channel.basicConsume(RabbitmqQueueConfig.FINANCIAL_BUY, false, new FinancialBuyConsumerProcessor(channel));
        }catch (Exception e) {
            logger.error("init financial buy consumer fail.", e);
        }
    }

    class FinancialBuyConsumerProcessor extends DefaultConsumer {
        private Channel channel;
        public FinancialBuyConsumerProcessor(Channel channel) {
            super(channel);
            this.channel = channel;
        }
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
            try {
                logger.info("[FinancialBuyConsumerProcessor] get an message:{}", JSON.toJSONString(body));
                RepertoryIn repertoryIn = JSON.parseObject(body, RepertoryIn.class);
                if (repertoryIn == null) {
                    logger.error("repertoryIn order parse result is null.");
                    return;
                }
                RepertoryIn order = repertoryInMapper.getByIdWithSupplierInfo(repertoryIn.getId());
                financialService.createFlowByBuyOrder(order);
            }catch (Exception e) {
                logger.error("financial buy order mq message process have exception.", e);
            }finally {
                try {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (IOException e) {
                    logger.error("financial buy order mq message ack fail.", e);
                }
            }
        }
    }
}
