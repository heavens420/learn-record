package com.zlx.demo.mqdemo.controller;

import com.chinaunicom.security.util.TokenDecoderUtils;
import com.zlx.demo.mqdemo.mqUtil.RocketMqCustomer;
import com.zlx.demo.mqdemo.mqUtil.RocketMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MqController {

    @Autowired
    private RocketMqProducer producer;

    @Autowired
    private RocketMqCustomer consumer;

    @RequestMapping("/**")
    public String test() throws Exception {
        String s = TokenDecoderUtils.generateToken("dsd", "fdsf", "fsdfsd");
        System.out.println(s);
        log.info("Test=============={}",s);
        return "test";
    }

    @GetMapping("/send")
    public String sendMsg(String topicName, String key, String msg) throws Exception {
        producer.getProducer();
        return producer.sendMsg(topicName, key, msg) ? "success" : "fail";
    }


    @GetMapping("/push")
    public void consumeMsg(String groupName, String topicName, String key) throws Exception {
        DefaultMQPushConsumer rocketMQPushConsumer = consumer.createRocketMQPushConsumer(groupName, topicName, null, null, null);
        rocketMQPushConsumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            try {
                if (CollectionUtils.isEmpty(msgs)) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                for (MessageExt msg : msgs) {
                    String value = new String(msg.getBody());
                    String tags = msg.getTags();
                    log.info("tag:{},msg:{}", tags, value);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (Exception e) {
                //延迟重新消费
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
    }


    @GetMapping("/pull")
    public void consumeMsg(String groupName, String topicName) throws Exception {
        DefaultLitePullConsumer rocketMQLitePullConsumer = consumer.createRocketMQLitePullConsumer(groupName, topicName, null, null, null);
        consumer.doConsumerMessage(rocketMQLitePullConsumer, topicName, null, msg -> {
            log.info("topic:{},msg:{} ", topicName, new String(msg.getBody()));
        });
    }


    public static void main(String[] args) {

    }
}
