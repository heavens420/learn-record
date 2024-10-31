package com.zlx.demo.mqdemo.mqUtil;


import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
@Slf4j
@Getter
public class RocketMqCustomer {
    @Autowired
    public MqConfigValues mqConfigValues;


    public DefaultMQPushConsumer getPushConsumer(String groupName, String topic, String tag,
                                                 MessageModel messageModel, String instanceName) {
        try {
            return createRocketMQPushConsumer(groupName, topic, tag, messageModel, instanceName);
        } catch (Exception e) {
            log.error("创建PushConsumer消费者异常", e);
        }
        return null;
    }

    public DefaultMQPushConsumer getPushConsumer(String groupName, String topic, String tag,
                                                 MessageModel messageModel) {
        try {
            return createRocketMQPushConsumer(groupName, topic, tag, messageModel, "");
        } catch (Exception e) {
            log.error("创建PushConsumer消费者异常", e);
        }
        return null;
    }

    public DefaultLitePullConsumer getLitePullConsumer(String groupName, String topic, String tag,
                                                       MessageModel messageModel) {
        try {
            return createRocketMQLitePullConsumer(groupName, topic, tag, messageModel, "");
        } catch (Exception e) {
            log.error("创建LitePullConsumer消费者异常", e);
        }
        return null;
    }

    public DefaultLitePullConsumer getLitePullConsumer(String groupName, String topic, String tag,
                                                       MessageModel messageModel, String instanceName) {
        try {
            return createRocketMQLitePullConsumer(groupName, topic, tag, messageModel, instanceName);
        } catch (Exception e) {
            log.error("创建LitePullConsumer消费者异常", e);
        }
        return null;
    }

    public synchronized DefaultMQPushConsumer createRocketMQPushConsumer(String groupName, String topic, String tag,
                                                                         MessageModel messageModel, String instanceName) throws Exception {
        if (StringUtils.isEmpty(groupName)) {
            throw new Exception("未配置RocketMQ分组信息");
        }
        if (StringUtils.isEmpty(mqConfigValues.getAddr())) {
            throw new Exception("未配置RocketMQ服务地址信息");
        }
        //创建消费者
        DefaultMQPushConsumer consumer;
        //是否需要权限认证
        if (mqConfigValues.getAclEnable()) {
            RPCHook rpcHook = new AclClientRPCHook(new SessionCredentials(mqConfigValues.getAccessKey(), mqConfigValues.getSecretKey()));
            consumer = new DefaultMQPushConsumer(groupName, rpcHook, new AllocateMessageQueueAveragely());
        } else {
            consumer = new DefaultMQPushConsumer(groupName);
        }
        //设置NameServer地址
        consumer.setNamesrvAddr(mqConfigValues.getAddr());
        //设置实例名称
        if (StringUtils.isEmpty(instanceName)) {
            consumer.setInstanceName("consumer:" + groupName);
        } else {
            consumer.setInstanceName("consumer:" + instanceName);
        }
        //每次最大消费数
        consumer.setConsumeMessageBatchMaxSize(mqConfigValues.getConsumerMaxSize());
        // 从消息队列尾部开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //集群模式，广播模式重复消费
        consumer.setMessageModel(messageModel);
        //订阅Topic
        log.info("实例名称是：" + groupName);
        log.info("订阅主题是：" + topic);
        try {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(tag)) {
                consumer.subscribe(topic, tag);
            } else {
                consumer.subscribe(topic, "*");
            }
        } catch (MQClientException e) {
            log.error("订阅主题{}异常", topic, e);
        }
        return consumer;
    }

    public synchronized DefaultLitePullConsumer createRocketMQLitePullConsumer(String groupName, String topic, String tag,
                                                                               MessageModel messageModel, String instanceName) throws Exception {
        if (StringUtils.isEmpty(groupName)) {
            throw new Exception("未配置RocketMQ分组信息");
        }
        if (StringUtils.isEmpty(mqConfigValues.getAddr())) {
            throw new Exception("未配置RocketMQ服务地址信息");
        }
        //创建消费者
        DefaultLitePullConsumer consumer;
        //是否需要权限认证
        if (mqConfigValues.getAclEnable()) {
            RPCHook rpcHook = new AclClientRPCHook(new SessionCredentials(mqConfigValues.getAccessKey(), mqConfigValues.getSecretKey()));
            consumer = new DefaultLitePullConsumer(groupName, rpcHook);
        } else {
            consumer = new DefaultLitePullConsumer(groupName);
        }
        //设置NameServer地址
        consumer.setNamesrvAddr(mqConfigValues.getAddr());
        //设置实例名称
        if (StringUtils.isEmpty(instanceName)) {
            consumer.setInstanceName("consumer:" + groupName);
        } else {
            consumer.setInstanceName("consumer:" + instanceName);
        }
        //每次最大消费数
        consumer.setPullBatchSize(mqConfigValues.getConsumerMaxSize());
        // 从上一个消息提交点开始消费，已消费的不会再次消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //集群模式：消息发到一个分组 一个消息只被分组内一个消费者消费一次，广播模式：所有消费者都完整的消费所有的消息
        consumer.setMessageModel(MessageModel.BROADCASTING);
        //订阅Topic
        log.info("实例名称是：" + groupName);
        log.info("订阅主题是：" + topic);
        try {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(tag)) {
                consumer.subscribe(topic, tag);
            } else {
                consumer.subscribe(topic, "*");
            }
        } catch (MQClientException e) {
            log.error("订阅主题{},tag:{}异常", topic, tag, e);
        }
        return consumer;
    }

    public void doConsumerMessage(DefaultLitePullConsumer consumer, String topicName, String tag, Consumer<MessageExt> lambdaConsumer) {

        try {
            log.info("rocketmq 订阅的主题：{}，订阅的tag：{}，开始消费", topicName, tag);
            if (consumer == null) {
                log.warn("topic：{}，tag：{}，消费者对象为空,不进行消费", topicName, tag);
                return;
            }
            long num = 0L;
            consumer.start();
            while (true) {
                int interval = 1;
                int batchSize = 100;
//                CnccCache cnccCache = SpringContextUtilForMq.getBean(CnccCache.class);
//                Object object = cnccCache.get(topicName);
//                if (num % batchSize == 0) {
//                    log.info("从缓存中获取mq限流信息：{}", JSONObject.toJSONString(object));
//                }
//                if (Objects.nonNull(object)) {
//                    Map<String, String> rateControl = JSONObject.parseObject(object.toString(), Map.class);
//                    String asyncConsumerRate = rateControl.get("asyncConsumerRate");
//                    if (org.apache.commons.lang3.StringUtils.isNotBlank(asyncConsumerRate)) {
//                        String[] split = asyncConsumerRate.split("/");
//                        if (split.length == 2) {
//                            interval = Integer.parseInt(split[1]);
//                            batchSize = Integer.parseInt(split[0]);
//                        }
//                    }
//                }
                List<MessageExt> messageExtList = consumer.poll(3000);
                if (messageExtList.isEmpty()) {
                    TimeUnit.SECONDS.sleep(interval);
                    continue;
                }
                for (MessageExt messageExt : messageExtList) {
                    lambdaConsumer.accept(messageExt);
                }
                if (++num % batchSize == 0) {
                    TimeUnit.SECONDS.sleep(interval);
                }
            }
        } catch (MQClientException e) {
            log.error("消费rocketmq消息异常,topic:{},tag:{}", topicName, tag, e);
        } catch (InterruptedException e) {
            log.error("sleep 中断异常：", e);
        }

    }
}
