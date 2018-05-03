package com.yiban.erp.config;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yiban.erp.entities.RepertoryIn;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitmqQueueConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitmqQueueConfig.class);

    @Value("${rabbitmq.host}")
    private String host;
    @Value("${rabbitmq.port}")
    private int port;
    @Value("${rabbitmq.username}")
    private String username;
    @Value("${rabbitmq.password}")
    private String password;
    @Value("${rabbitmq.virtual-host}")
    private String vhost;

    // topic exchange name
    public static final String TOPIC_TYPE = "topic";
    public static final String EXCHANGE = "ERP-TOPIC";

    // queue name
    public static final String FINANCIAL_SELL = "FINANCIAL.SELL";
    public static final String FINANCIAL_SELL_BACK = "FINANCIAL.SELL.BACK";
    public static final String FINANCIAL_BUY = "FINANCIAL.BUY";
    public static final String FINANCIAL_BUY_BACK = "FINANCIAL.BUY.BACK";

    // route key
    public static final String ORDER_ALL = "order.#";   //所有的订单相关的事件route key
    public static final String ORDER_SELL = "order.sell";  //销售订单 route key
    public static final String ORDER_SELL_BACK = "order.sell.back"; //销售退货 route key
    public static final String ORDER_BUY = "order.buy"; //采购入库
    public static final String ORDER_BUY_BACK = "order.buy.back"; //采购退货

    private Connection connection;
    private Map<String, Channel> channelMap;


    @PostConstruct
    public Connection getConnectionFactory() {
        if (this.connection != null && this.connection.isOpen()) {
            return connection;
        }
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setVirtualHost(vhost);
            connection = factory.newConnection();
            return connection;
        }catch (Exception e) {
            logger.error("rabbitmq connection create fail.", e);
            throw new BizRuntimeException(ErrorCode.RABBITMQ_CONNECTION_FAIL);
        }
    }

    public Channel getSendChannel(String beanName) {
        if (channelMap == null) {
            channelMap = new HashMap<>();
        }
        try {
            Channel result = channelMap.get(beanName);
            if (result == null || !result.isOpen()) {
                result = getConnectionFactory().createChannel();
                /**
                 * 参数1：交换器名称
                 * 参数2：交换模式 topic
                 * 参数3：交换器持久性，如果为true则服务器重启时不会丢失
                 * 参数4：交换器在不被使用时是否删除
                 * 参数5：交换器的其他属性
                 */
                result.exchangeDeclare(RabbitmqQueueConfig.EXCHANGE, RabbitmqQueueConfig.TOPIC_TYPE, true, false, null);
                channelMap.put(beanName, result);
            }
            return result;
        }catch (Exception e) {
            logger.error("get beanName:{} channel fail.", beanName, e);
            throw new BizRuntimeException(ErrorCode.RABBITMQ_CONNECTION_FAIL);
        }
    }

    public <T> void sendMessage(String beanName, String routeKey, T body) {
        try {
            Channel channel = getSendChannel(beanName);
            /**
             * 参数1：交换器名称
             * 参数2：路由 route key
             * 参数3：其他配置
             * 参数4：消息体(注意可以序列化)
             */
            channel.basicPublish(RabbitmqQueueConfig.EXCHANGE, routeKey, null, JSON.toJSONBytes(body));
        }catch (Exception e) {
            logger.error("send beanName:{} mq message fail.", beanName, e);
            throw new BizRuntimeException(ErrorCode.RABBITMQ_SEND_MESSAGE_FAIL);
        }
    }

}
