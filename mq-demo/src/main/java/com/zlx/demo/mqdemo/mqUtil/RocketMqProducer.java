package com.zlx.demo.mqdemo.mqUtil;

import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Getter
public class RocketMqProducer {
    @Value("${logicNeId:empty}")
    private String logicNeId;

    @Value(value = "${mqRetryNum:1}")
    private int mqRetryNum;

    @Value(value = "${mqRetryDelay:0}")
    private Long mqRetryDelay;

    @Value(value = "${mqTimeout:1000}")
    private Long mqTimeout;

    private String groupName;

    public DefaultMQProducer producer;

    @Autowired
    private MqConfigValues mqConfigValues;

    public DefaultMQProducer getProducer() throws Exception {
        if (producer == null) {
            producer = createRocketMQProducer();
        }
        return producer;
    }

    public synchronized DefaultMQProducer createRocketMQProducer() throws Exception {
        if (!"empty".equals(this.logicNeId)) {
            this.groupName = mqConfigValues.getProducerGroupName() + this.logicNeId;
        } else {
            this.groupName = mqConfigValues.getProducerGroupName();
        }
        if (StringUtils.isEmpty(this.groupName)) {
            throw new Exception("未配置RocketMQ分组信息");
        }
        if (StringUtils.isEmpty(mqConfigValues.getAddr())) {
            throw new Exception("未配置RocketMQ服务地址信息");
        }
        DefaultMQProducer producer;
        if (mqConfigValues.getAclEnable()) {
            RPCHook rpcHook = new AclClientRPCHook(new SessionCredentials(mqConfigValues.getAccessKey(), mqConfigValues.getSecretKey()));
            producer = new DefaultMQProducer(this.groupName, rpcHook);
        } else {
            producer = new DefaultMQProducer(this.groupName);
        }
        producer.setNamesrvAddr(mqConfigValues.getAddr());
        if (mqConfigValues.getMaxMessageSize() != null) {
            producer.setMaxMessageSize(mqConfigValues.getMaxMessageSize());
        }
        if (mqConfigValues.getSendMsgTimeout() != null) {
            producer.setSendMsgTimeout(Integer.parseInt(mqConfigValues.getSendMsgTimeout()));
        }
        //如果发送消息失败，设置重试次数，默认为2次
        if (mqConfigValues.getRetryTimesWhenSendFailed() != null) {
            producer.setRetryTimesWhenSendFailed(mqConfigValues.getRetryTimesWhenSendFailed());
        }

        try {
            producer.start();
            log.info(String.format("producer is start ! groupName:[%s],namesrvAddr:[%s]"
                    , this.groupName, mqConfigValues.getAddr()));
        } catch (MQClientException e) {
            log.error(String.format("producer is error {}"
                    , e.getMessage(), e));
            throw new Exception(e);
        }
        return producer;
    }

    public boolean executeSendMsg(Message message, boolean flag) {
        ScheduledFuture<SendResult> schedule;
        String topic = message.getTopic();
        String tag = message.getTags();
        String key = message.getKeys();
        String msg = new String(message.getBody());
        @Cleanup(value = "shutdown") final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        for (int i = 1; i < mqRetryNum + 1; i++) {
            try {
                // 首次执行不需要延迟
                if (i == 1) {
                    schedule = executorService.schedule(() -> producer.send(message), 0, TimeUnit.SECONDS);
                } else {
                    // 非首次执行 延迟执行
                    schedule = executorService.schedule(() -> producer.send(message), mqRetryDelay, TimeUnit.SECONDS);
                }
                SendResult result = schedule.get(mqTimeout, TimeUnit.SECONDS);
                if (result != null && result.getSendStatus() == SendStatus.SEND_OK) {
                    log.info("第{}次尝试, 入队成功, 主题名: {},tag: {}, key: {}, msg: {}", i, topic, tag, key, message);
                    flag = true;
                    break;
                } else {
                    log.info("第{}次尝试,入队失败, 主题名: {},tag: {}, key: {}, msg: {}, 尝试重新入队", i, topic, tag, key, message);
                }
            } catch (Exception e) {
//                ProducerUtil.sendFailHandle();
                log.error("入队异常, 主题名: {},tag:{}, key: {}, msg: {}", topic, tag, key, msg, e);
            }
        }
        return flag;
    }

    public boolean sendMsg(String topicName, String key, String msg) {
        boolean flag = false;

        Message message = new Message(topicName, null, key, msg.getBytes());
        if (producer == null) {
            log.warn("生产者对象还未创建, 请先创建对象");
        } else {
            flag = executeSendMsg(message, flag);
        }

        return flag;
    }

    public boolean sendMsg(String topicName, String key, String msg, Integer delayTimeLevel) {

        Message message = new Message(topicName, null, key, msg.getBytes());
        if (delayTimeLevel == null) {
            log.error("MQ设置延迟消息delayTime为NULL");
            return false;
        }
        message.setDelayTimeLevel(delayTimeLevel);
        boolean flag = false;
        if (producer == null) {
            log.warn("生产者对象还未创建, 请先创建对象");
        } else {
            flag = executeSendMsg(message, flag);
//            for (int i = 1; i < mqRetryNum + 1; i++) {
//                try {
//                    SendResult result = producer.send(message);
//                    if (result != null && result.getSendStatus() == SendStatus.SEND_OK) {
//                        log.info("入队成功, 主题名: {}, key: {}", topicName, key);
//                        flag = true;
//                        break;
//                    } else {
//                        log.info("入队失败, 主题名: {}, key: {}, 尝试重新入队", topicName, key);
//                        Thread.sleep(mqRetryDelay);
//                        result = producer.send(message);
//                        if (result != null && result.getSendStatus() == SendStatus.SEND_OK) {
//                            log.info("重新入队成功, 主题名: {}, key: {}", topicName, key);
//                            flag = true;
//                            break;
//                        } else {
//                            log.warn("重新入队失败, 主题名: {}, key: {}", topicName, key);
//                            flag = false;
//                        }
//                    }
//                } catch (Exception e) {
//                    ProducerUtil.sendFailHandle();
//                    log.error("入队异常, 主题名: {}, key: {}, 尝试第{}次重新入队", topicName, key, i, e);
//                }
//                try {
//                    Thread.sleep(mqRetryDelay);
//                } catch (InterruptedException e) {
//                    log.error("重新入队失败休眠异常", e);
//                }
//            }
        }
        if (!flag) {
//            ProducerUtil.sendFailHandle();
        }
        return flag;
    }

    /**
     * 发送带tag的消息，方便过滤
     *
     * @return void
     * @date 10:06 2020/12/9
     * @Param [topicName, key, msg, tag]
     */
    public boolean sendMsg(String topicName, String key, String msg, String tag) {

        Message message = new Message(topicName, tag, key, msg.getBytes());
        boolean flag = false;
        if (producer == null) {
            log.warn("生产者对象还未创建, 请先创建对象");
        } else {
            flag = executeSendMsg(message, flag);
        }
        if (!flag) {
//            ProducerUtil.sendFailHandle();
        }
        return flag;
    }

    public boolean sendMsg(String topicName, String key, String msg, String tag, Integer delayTimeLevel) {
        Message message = new Message(topicName, tag, key, msg.getBytes());
        if (delayTimeLevel == null) {
            log.error("MQ设置延迟消息delayTime为NULL");
            return false;
        }
        message.setDelayTimeLevel(delayTimeLevel);
        boolean flag = false;
        if (producer == null) {
            log.warn("生产者对象还未创建, 请先创建对象");
        } else {
            flag = executeSendMsg(message, flag);
        }
        if (!flag) {
//            ProducerUtil.sendFailHandle();
        }
        return flag;
    }

    /**
     * 增加根据params计算，入对应的分区队列
     *
     * @param topicName
     * @param key
     * @param value
     * @param params
     * @return
     */
    public boolean sendMsg(String topicName, String key, String value, List<String> params) {
        String param = !CollectionUtils.isEmpty(params) ? transParam(params) : "";
        return this.sendMsg(topicName, key, value, null, param);
    }

    /**
     * 增加根据params计算，入对应的分区队列
     *
     * @param topicName
     * @param key
     * @param value
     * @param tag
     * @param params
     * @return
     */
    public boolean sendMsg(String topicName, String key, String value, String tag, String params) {
        return this.sendMsg(topicName, key, value, tag, null, params);
    }

    /**
     * 增加根据params计算，入对应的分区队列
     *
     * @param topicName
     * @param key
     * @param msg
     * @param tag
     * @param delayTimeLevel
     * @param params
     * @return
     */
    public boolean sendMsg(String topicName, String key, String msg, String tag, Integer delayTimeLevel, String params) {

        Message message = new Message(topicName, tag, key, msg.getBytes());
        if (delayTimeLevel != null) {
            message.setDelayTimeLevel(delayTimeLevel);
        }

        boolean flag = false;
        if (producer == null) {
            log.warn("生产者对象还未创建, 请先创建对象");
        } else {
            flag = executeSendMsg(message, flag);
        }
        if (!flag) {
//            ProducerUtil.sendFailHandle();
        }
        return flag;
    }

    public static String transParam(List<String> params) {
        StringBuffer buffer = new StringBuffer();
        for (String param : params) {
            buffer.append(param);
        }
        return buffer.toString();
    }

}
