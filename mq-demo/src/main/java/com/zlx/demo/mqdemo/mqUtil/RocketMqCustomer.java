package com.zlx.demo.mqdemo.mqUtil;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.*;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

    /**
     * ctgmq不支持 需用原生rocketmq
     *
     * @param groupName
     * @param topic
     * @param tag
     * @param messageModel
     * @return
     */
    public DefaultLitePullConsumer getLitePullConsumer(String groupName, String topic, String tag,
                                                       MessageModel messageModel) {
        try {
            return createRocketMQLitePullConsumer(groupName, topic, tag, messageModel, "");
        } catch (Exception e) {
            log.error("创建LitePullConsumer消费者异常", e);
        }
        return null;
    }

    public DefaultMQPullConsumer getPullConsumer(String groupName, MessageModel messageModel) {
        try {
            return createRocketMQPullConsumer(groupName, messageModel, "");
        } catch (Exception e) {
            log.error("创建PullConsumer消费者异常", e);
        }
        return null;
    }

    public DefaultMQPullConsumer getPullConsumer(String groupName, MessageModel messageModel, String instanceName) {
        try {
            return createRocketMQPullConsumer(groupName, messageModel, instanceName);
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
            log.error("订阅主题{},tag:{}异常", topic, tag, e);
        }
        return consumer;
    }

    public synchronized DefaultMQPullConsumer createRocketMQPullConsumer(String groupName, MessageModel messageModel, String instanceName) throws Exception {
        if (StringUtils.isEmpty(groupName)) {
            throw new Exception("未配置RocketMQ分组信息");
        }
        if (StringUtils.isEmpty(mqConfigValues.getAddr())) {
            throw new Exception("未配置RocketMQ服务地址信息");
        }
        //创建消费者
        DefaultMQPullConsumer consumer;
        //是否需要权限认证
        if (mqConfigValues.getAclEnable()) {
            RPCHook rpcHook = new AclClientRPCHook(new SessionCredentials(mqConfigValues.getAccessKey(), mqConfigValues.getSecretKey()));
            consumer = new DefaultMQPullConsumer(groupName, rpcHook);
        } else {
            consumer = new DefaultMQPullConsumer(groupName);
        }
        //设置NameServer地址
        consumer.setNamesrvAddr(mqConfigValues.getAddr());
        //设置实例名称
        if (StringUtils.isEmpty(instanceName)) {
            consumer.setInstanceName("consumer:" + groupName);
        } else {
            consumer.setInstanceName("consumer:" + instanceName);
        }
        // 从上一个消息提交点开始消费，已消费的不会再次消费
//        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //集群模式，广播模式重复消费
        consumer.setMessageModel(messageModel);
        //订阅Topic
        log.info("实例名称是：" + groupName);
        return consumer;
    }

    public void doConsumerMessage(DefaultMQPullConsumer consumer, String topicName, String tag, Consumer<MessageExt> lambdaConsumer) {
        try {
            log.info("rocketmq 订阅的主题：{}，订阅的tag：{}，开始消费", topicName, tag);
            if (consumer == null) {
                log.warn("topic：{}，tag：{}，消费者对象为空,不进行消费", topicName, tag);
                return;
            }
            long num = 0L;
            consumer.start();

            // 本地缓存 用于保存队列的offset
            // 为什么不直接用fetchConsumeOffset方法：
            // 1 fetchConsumeOffset通过rpc请求获取最新offset，需要耗费时间，而且需要网络请求，性能上不如本地缓存
            // 2 经实测fetchConsumeOffset无法及时获取最新的offset也或者是updateConsumeOffset无法及时更新最新的offset导致重复消费
            // 3 为了更高的消费效率，可对每个队列创建一个线程消费，本地缓存保存offset可保证offset的准确性，不会出现消息重复消费的问题
            Map<MessageQueue, Long> offsetTable = new ConcurrentHashMap<>();

            // 获取消息队列
            Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(topicName);

            // 初始化队列的起始offset
            for (MessageQueue queue : mqs) {
                long offset = consumer.fetchConsumeOffset(queue, true);
                if (offset < 0) {
                    offset = 0;
                }
                offsetTable.put(queue, offset);
            }

            while (true) {
                for (MessageQueue queue : mqs) {
                    MessageExt tempMsg = new MessageExt();
                    try {
                        // 获取队列偏移量
                        long offset = offsetTable.get(queue);
                        // 拉取消息
//                        PullResult pullResult = consumer.pullBlockIfNotFound(queue, tag, offset, 32);
                        PullResult pullResult = consumer.pull(queue, tag, offset, 32);
                        if (pullResult.getPullStatus() == PullStatus.FOUND) {
                            List<MessageExt> msgFoundList = pullResult.getMsgFoundList();
                            for (MessageExt messageExt : msgFoundList) {
                                // 更新消费进度
                                consumer.updateConsumeOffset(queue, offset);
                                tempMsg = messageExt;
                                try {
                                    lambdaConsumer.accept(messageExt);
                                } catch (Exception e) {
                                    log.error("topic={},tag={},msg:{},处理rocketmq消息异常:", topicName, tag, new String(messageExt.getBody(), StandardCharsets.UTF_8), e);
                                }
                                // 计数加1
                                num++;
                            }
                        }
                        offset = pullResult.getNextBeginOffset();
                        consumer.updateConsumeOffset(queue, offset);
                        offsetTable.put(queue, offset);
                    } catch (Exception e) {
                        log.error("topic={},tag={},msg={},消费消息异常：", topicName, tag, tempMsg, e);
                    }
                }
            }
        } catch (MQClientException e) {
            log.error("消费rocketmq消息异常,topic:{},tag:{}", topicName, tag, e);
        }
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
                List<MessageExt> messageExtList = consumer.poll(3000);
                if (messageExtList.isEmpty()) {
//                    TimeUnit.SECONDS.sleep(interval);
                    continue;
                }
                for (MessageExt messageExt : messageExtList) {
                    try {
                        lambdaConsumer.accept(messageExt);
                    } catch (Exception e) {
                        log.error("topic={},tag={},msg={},消费消息异常：", topicName, tag, new String(messageExt.getBody()), e);
                    }
                }

            }
        } catch (MQClientException e) {
            log.error("消费rocketmq消息异常,topic:{},tag:{}", topicName, tag, e);
        }

    }


    @PostConstruct
    public void consumerMessage() throws Exception {
        log.info("begign--------------------------------");
//        DefaultMQPullConsumer rocketMQPushConsumer = this.createRocketMQPullConsumer("C_T_CHECK_EWS_XML", MessageModel.CLUSTERING, "consumer:C_T_CHECK_EWS_XML");
//        this.doConsumerMessage(rocketMQPushConsumer,"T_CHECK_EWS_XML","",msg -> {
//            log.info("------------");
//        });
//
//        DefaultMQPullConsumer rocketMQPushConsumer1 = this.createRocketMQPullConsumer("C_T_CHECK_EWS", MessageModel.CLUSTERING, "consumer:C_T_CHECK_EWS");
//        this.doConsumerMessage(rocketMQPushConsumer1, "T_CHECK_EWS", "", msg -> {
//            log.info("------------");
//        });

        DefaultMQPullConsumer rocketMQPushConsumer = this.createRocketMQPullConsumer("DefaultCluster", MessageModel.CLUSTERING, "");
        this.doConsumerMessage(rocketMQPushConsumer, "testTopic", "", msg -> {
            log.info("------------msg:{}", new String(msg.getBody(), StandardCharsets.UTF_8));
        });

    }
}
