package com.yiban.erp;

import com.yiban.erp.config.RabbitmqQueueConfig;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class RabbitmqTest {

    @Autowired
    private RabbitmqQueueConfig rabbitmqQueueConfig;

    @Test
    public void testSendSellMessage() {
        rabbitmqQueueConfig.sendMessage("TEST", RabbitmqQueueConfig.ORDER_SELL, "sell order message test. 1");
        rabbitmqQueueConfig.sendMessage("TEST", RabbitmqQueueConfig.ORDER_SELL, "sell order message test. 2");
        rabbitmqQueueConfig.sendMessage("TEST", RabbitmqQueueConfig.ORDER_SELL, "sell order message test. 3");
        rabbitmqQueueConfig.sendMessage("TEST", RabbitmqQueueConfig.ORDER_SELL, "sell order message test. 4");
        rabbitmqQueueConfig.sendMessage("TEST", RabbitmqQueueConfig.ORDER_SELL, "sell order message test. 5");

        try {
            Thread.sleep(8000);
            System.out.println("Test thread End.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
