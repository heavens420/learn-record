//package com.zlx.demo.mqdemo.mqUtil;
//
//import com.ctg.mq.api.CTGMQFactory;
//import com.ctg.mq.api.IMQProducer;
//import com.ctg.mq.api.PropertyKeyConst;
//import com.ctg.mq.api.bean.*;
//import com.ctg.mq.api.exception.MQException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.CollectionUtils;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ThreadLocalRandom;
//
///**
// * 一个不依赖于spring的工具类, 用来给控制的南北向去调用
// *
// * @author : liu.guangyao
// * @date : 2020-06-17
// */
//public class ProducerUtil {
//    private final static Logger log = LoggerFactory.getLogger(ProducerUtil.class);
//
//    /**
//     * 私有构造函数
//     */
//    private ProducerUtil() {
//    }
//
//    private static boolean sizeEnable = false;
//
//    private static int maxSize = 2097152;
//
//    private static int mqRetryNum = 1;
//
//    private static Long mqRetryDelay = 0L;
//
//    /**
//     * 生产者
//     */
//    public static IMQProducer producer = null;
//
//    public static Map<Integer, IMQProducer> producerMap = new ConcurrentHashMap<>();
//
//    public static IMQProducer getProducer() {
//        if (CollectionUtils.isEmpty(producerMap)) {
//            throw new RuntimeException("集团CtgMq生产者初始化为空！");
//        }
//        int i = ThreadLocalRandom.current().nextInt(producerMap.size());
//        return producerMap.get(i);
//    }
//
//
//    @Deprecated
//    public static void createProducer(String producerGroupName, String addr, String authId, String authPwd, String clusterName,
//                                      String tenantId, String sendMsgTimeout, boolean vipChannelEnabled, int producerNum) throws Exception {
//        Properties properties = new Properties();
//        for (int i = 0; i < producerNum; i++) {
//            properties.setProperty(PropertyKeyConst.ProducerGroupName, producerGroupName + "_" + i);
//            properties.setProperty(PropertyKeyConst.NamesrvAddr, addr);
//            properties.setProperty(PropertyKeyConst.NamesrvAuthID, authId);
//            properties.setProperty(PropertyKeyConst.NamesrvAuthPwd, authPwd);
//            properties.setProperty(PropertyKeyConst.ClusterName, clusterName);
//            properties.setProperty(PropertyKeyConst.TenantID, tenantId);
//            properties.setProperty(PropertyKeyConst.VipChannelEnabled, String.valueOf(vipChannelEnabled));
//            properties.setProperty(PropertyKeyConst.SendMsgTimeout, sendMsgTimeout);
//            log.info("连接ip: {}", addr);
//            IMQProducer producer = CTGMQFactory.createProducer(properties);
//            try {
//                producer.connect();
//                log.info("集团CtgMq生产者[{}]启动成功", producerGroupName + "_" + i);
//            } catch (Exception e) {
//                log.error("调度模块生产者连接异常", e);
//                throw e;
//            }
//            producerMap.put(i, producer);
//        }
//    }
//
//    /**
//     * 根据配置文件初始化生产者
//     */
//    public static void createProducer(MqConfigValues mqConfigValues) throws Exception {
//        if (mqConfigValues.getProducerNumber() < 1) {
//            throw new IllegalArgumentException("生产者数量必须为正整数");
//        }
//        mqRetryNum = mqConfigValues.getMqRetryNum();
//        mqRetryDelay = mqConfigValues.getMqRetryDelay();
//        Properties properties = new Properties();
//        for (int i = 0; i < mqConfigValues.getProducerNumber(); i++) {
//            properties.setProperty(PropertyKeyConst.ProducerGroupName, mqConfigValues.getProducerGroupName() + "_" + i);
//            properties.setProperty(PropertyKeyConst.NamesrvAddr, mqConfigValues.getAddr());
//            properties.setProperty(PropertyKeyConst.NamesrvAuthID, mqConfigValues.getAuthId());
//            properties.setProperty(PropertyKeyConst.NamesrvAuthPwd, mqConfigValues.getAuthPwd());
//            properties.setProperty(PropertyKeyConst.ClusterName, mqConfigValues.getClusterName());
//            properties.setProperty(PropertyKeyConst.TenantID, mqConfigValues.getTenantId());
//            properties.setProperty(PropertyKeyConst.VipChannelEnabled, String.valueOf(mqConfigValues.getVipChannelEnabled()));
//            properties.setProperty(PropertyKeyConst.SendMsgTimeout, mqConfigValues.getSendMsgTimeout());
//
//            properties.setProperty(PropertyKeyConst.SendMaxRetryTimes, "13");
//
//
//            log.info("连接ip: {}", mqConfigValues.getAddr());
//            IMQProducer producer = CTGMQFactory.createProducer(properties);
//            try {
//                producer.connect();
//                log.info("集团CtgMq生产者[{}]启动成功", mqConfigValues.getProducerGroupName() + "_" + i);
//            } catch (Exception e) {
//                log.error("调度模块生产者连接异常", e);
//                throw e;
//            }
//            producerMap.put(i, producer);
//        }
//    }
//
//    /**
//     * 根据配置文件初始化生产者
//     * 可自定义生产者实例个数
//     */
//    public static void createProducer(MqConfigValues mqConfigValues, int producerNum) throws Exception {
//        if (producerNum < 1) {
//            throw new IllegalArgumentException("生产者数量必须为正整数");
//        }
//        mqRetryNum = mqConfigValues.getMqRetryNum();
//        mqRetryDelay = mqConfigValues.getMqRetryDelay();
//        Properties properties = new Properties();
//        for (int i = 0; i < producerNum; i++) {
//            properties.setProperty(PropertyKeyConst.ProducerGroupName, mqConfigValues.getProducerGroupName() + "_" + i);
//            properties.setProperty(PropertyKeyConst.NamesrvAddr, mqConfigValues.getAddr());
//            properties.setProperty(PropertyKeyConst.NamesrvAuthID, mqConfigValues.getAuthId());
//            properties.setProperty(PropertyKeyConst.NamesrvAuthPwd, mqConfigValues.getAuthPwd());
//            properties.setProperty(PropertyKeyConst.ClusterName, mqConfigValues.getClusterName());
//            properties.setProperty(PropertyKeyConst.TenantID, mqConfigValues.getTenantId());
//            properties.setProperty(PropertyKeyConst.VipChannelEnabled, String.valueOf(mqConfigValues.getVipChannelEnabled()));
//            properties.setProperty(PropertyKeyConst.SendMsgTimeout, mqConfigValues.getSendMsgTimeout());
//
//            properties.setProperty(PropertyKeyConst.SendMaxRetryTimes, "13");
//
//
//            log.info("连接ip: {}", mqConfigValues.getAddr());
//            IMQProducer producer = CTGMQFactory.createProducer(properties);
//            try {
//                producer.connect();
//                log.info("集团CtgMq生产者[{}]启动成功", mqConfigValues.getProducerGroupName() + "_" + i);
//            } catch (Exception e) {
//                log.error("调度模块生产者连接异常", e);
//                throw e;
//            }
//            producerMap.put(i, producer);
//        }
//    }
//
//    public static boolean push(String topicName, String key, String value) {
//        if (!SendMessageJudgeUtil.isOpen) {
//            return false;
//        }
//        boolean flag = false;
//        producer = getProducer();
//        int valLength = value.getBytes(StandardCharsets.UTF_8).length;
//        if (!sizeEnable || valLength < maxSize) {
//            if (producer == null) {
//                log.warn("生产者对象还未创建, 请先创建对象");
//                flag = false;
//            } else {
//                // 该参数代表送往哪个队列名
//                MQMessage message = new MQMessage(topicName, key, null, value.getBytes());
//                for(int i = 0;i < mqRetryNum + 1; i++){
//                    try {
//                        MQSendResult sendResult = producer.send(message);
//                        if (sendResult != null && sendResult.getSendStatus() == MQSendStatus.SEND_OK) {
//                            log.info("入队成功, 主题名: {},key: {}", topicName,key);
//                            flag = true;
//                            break;
//                        } else {
//                            log.warn("入队失败, 主题名: {}, value: {}", topicName, value);
//                            log.info("尝试重新入队, 主题名: {}", topicName);
//                            MQSendResult sendResult1 = producer.send(message);
//                            if (sendResult1 == null || sendResult1.getSendStatus() != MQSendStatus.SEND_OK) {
//                                log.warn("重新入队失败");
//                                flag = false;
//                            } else {
//                                log.info("重新入队成功");
//                                flag = true;
//                                break;
//                            }
//                        }
//                    } catch (Exception e) {
//                        sendFailHandle();
//                        log.error("重新入队异常, 尝试第{}次重新入队", i, e);
//                    }
//                    try {
//                        Thread.sleep(mqRetryDelay);
//                    } catch (InterruptedException e) {
//                        log.error("重新入队失败休眠异常",e);
//                    }
//                }
//            }
//            if (!flag) {
//                sendFailHandle();
//            }
//        } else {
//            flag = false;
//            log.info("消息入队异常，topicName：{}，消息体大小：{}，消息最大值:{}，消息丢弃。", topicName, valLength, maxSize);
//        }
//        return flag;
//    }
//
//
//    public static boolean push(String topicName, String key, String value, Integer delayTimeLevel) {
//        if (!SendMessageJudgeUtil.isOpen) {
//            return false;
//        }
//        boolean flag = false;
//        producer = getProducer();
//        int valLength = value.getBytes(StandardCharsets.UTF_8).length;
//        if (!sizeEnable || valLength < maxSize) {
//            if (producer == null) {
//                log.warn("生产者对象还未创建, 请先创建对象");
//                flag = false;
//            } else {
//                // 该参数代表送往哪个队列名
//                MQMessage message = new MQMessage(topicName, key, null, value.getBytes());
//                if (delayTimeLevel == null) {
//                    log.error("MQ设置延迟消息delayTime为NULL");
//                    return false;
//                }
//                message.setDelayTimeLevel(delayTimeLevel);
//                for(int i = 0;i < mqRetryNum + 1; i++){
//                    try {
//                        MQSendResult sendResult = producer.send(message);
//                        if (sendResult != null && sendResult.getSendStatus() == MQSendStatus.SEND_OK) {
//                            log.info("入队成功, 主题名: {}", topicName);
//                            flag = true;
//                            break;
//                        } else {
//                            log.warn("入队失败, 主题名: {}, value: {}", topicName, value);
//                            log.info("尝试重新入队, 主题名: {}", topicName);
//                            MQSendResult sendResult1 = producer.send(message);
//                            if (sendResult1 == null || sendResult1.getSendStatus() != MQSendStatus.SEND_OK) {
//                                log.warn("重新入队失败");
//                                flag = false;
//                            } else {
//                                log.info("重新入队成功");
//                                flag = true;
//                                break;
//                            }
//                        }
//                    } catch (Exception e) {
//                        sendFailHandle();
//                        log.error("重新入队异常, 尝试第{}次重新入队", i, e);
//                    }
//                    try {
//                        Thread.sleep(mqRetryDelay);
//                    } catch (InterruptedException e) {
//                        log.error("重新入队失败休眠异常",e);
//                    }
//                }
//            }
//            if (!flag) {
//                sendFailHandle();
//            }
//        } else {
//            flag = false;
//            log.info("消息入队异常，topicName：{}，消息体大小：{}，消息最大值:{}，消息丢弃。", topicName, valLength, maxSize);
//        }
//        return flag;
//    }
//
//    /**
//     * 推送异常处理
//     */
//    static void sendFailHandle() {
//        SendMessageJudgeUtil judgeUtil = SpringContextUtilForMq.getBean(SendMessageJudgeUtil.class);
//        judgeUtil.exceptionHandle();
//    }
//
//    /**
//     * 发送带tag的消息，方便过滤
//     *
//     * @return void
//     * @date 10:02 2020/12/9
//     * @Param [topicName, key, value, tag]
//     */
//    public static boolean push(String topicName, String key, String value, String tag) {
//        if (!SendMessageJudgeUtil.isOpen) {
//            return false;
//        }
//        boolean flag = false;
//        int valLength = value.getBytes(StandardCharsets.UTF_8).length;
//        producer = getProducer();
//        if (!sizeEnable || valLength < maxSize) {
//            if (producer == null) {
//                log.warn("生产者对象还未创建, 请先创建对象");
//                flag = false;
//            } else {
//                // 该参数代表送往哪个队列名
//                MQMessage message = new MQMessage(topicName, key, tag, value.getBytes());
//                for(int i = 0;i < mqRetryNum + 1; i++){
//                    try {
//                        MQSendResult sendResult = producer.send(message);
//                        if (sendResult != null && sendResult.getSendStatus() == MQSendStatus.SEND_OK) {
//                            if (!"nti_api_data".equals(tag)) {
//                                log.info("入队成功, 主题名: {}, tag名: {}", topicName, tag);
//                            }
//                            flag = true;
//                            break;
//                        } else {
//                            log.warn("入队失败, 主题名: {}, tag名: {}, value: {}", topicName, tag, value);
//                            log.info("尝试重新入队, 主题名: {}", topicName);
//                            MQSendResult sendResult1 = producer.send(message);
//                            if (sendResult1 == null || sendResult1.getSendStatus() != MQSendStatus.SEND_OK) {
//                                log.info("重新入队失败");
//                                flag = false;
//                            } else {
//                                log.info("重新入队成功");
//                                flag = true;
//                                break;
//                            }
//                        }
//                    } catch (Exception e) {
//                        sendFailHandle();
//                        log.error("入队异常, 主题名: {} tag名: {},尝试第{}次重新入队,异常: {}",topicName,tag,i,e);
//                    }
//                    try {
//                        Thread.sleep(mqRetryDelay);
//                    } catch (InterruptedException e) {
//                        log.error("重新入队失败休眠异常",e);
//                    }
//                }
//            }
//            if (!flag) {
//                sendFailHandle();
//            }
//        } else {
//            flag = false;
//            log.info("消息入队异常，topicName：{}，tag:{}，消息体大小：{}，消息最大值:{}，消息丢弃。", topicName, tag, valLength, maxSize);
//        }
//        return flag;
//    }
//
//    public static boolean push(String topicName, String key, String value, String tag, Integer delayTimeLevel) {
//        if (!SendMessageJudgeUtil.isOpen) {
//            return false;
//        }
//        boolean flag = false;
//        int valLength = value.getBytes(StandardCharsets.UTF_8).length;
//        producer = getProducer();
//        if (!sizeEnable || valLength < maxSize) {
//            if (producer == null) {
//                log.warn("生产者对象还未创建, 请先创建对象");
//                flag = false;
//            } else {
//                // 该参数代表送往哪个队列名
//                MQMessage message = new MQMessage(topicName, key, tag, value.getBytes());
//                if (delayTimeLevel == null) {
//                    log.error("MQ设置延迟消息delayTime为NULL");
//                    return false;
//                }
//                message.setDelayTimeLevel(delayTimeLevel);
//                for(int i = 0;i < mqRetryNum + 1; i++){
//                    try {
//                        MQSendResult sendResult = producer.send(message);
//                        if (sendResult != null && sendResult.getSendStatus() == MQSendStatus.SEND_OK) {
//                            if (!"nti_api_data".equals(tag)) {
//                                log.info("入队成功, 主题名: {}, tag名: {}", topicName, tag);
//                            }
//                            flag = true;
//                            break;
//                        } else {
//                            log.warn("入队失败, 主题名: {}, tag名: {}, value: {}", topicName, tag, value);
//                            log.info("尝试重新入队, 主题名: {}", topicName);
//                            MQSendResult sendResult1 = producer.send(message);
//                            if (sendResult1 == null || sendResult1.getSendStatus() != MQSendStatus.SEND_OK) {
//                                log.info("重新入队失败");
//                                flag = false;
//                            } else {
//                                log.info("重新入队成功");
//                                flag = true;
//                                break;
//                            }
//                        }
//                    } catch (Exception e) {
//                        sendFailHandle();
//                        log.error("入队异常, 主题名: {} tag名: {},尝试第{}次重新入队,异常: {}",topicName,tag,i,e);
//                    }
//                    try {
//                        Thread.sleep(mqRetryDelay);
//                    } catch (InterruptedException e) {
//                        log.error("重新入队失败休眠异常",e);
//                    }
//                }
//            }
//            if (!flag) {
//                sendFailHandle();
//            }
//        } else {
//            flag = false;
//            log.info("消息入队异常，topicName：{}，tag:{}，消息体大小：{}，消息最大值:{}，消息丢弃。", topicName, tag, valLength, maxSize);
//        }
//        return flag;
//    }
//
//
//    public static void setMaxLimit(boolean enable, int maxSize) {
//        ProducerUtil.sizeEnable = enable;
//        ProducerUtil.maxSize = maxSize;
//    }
//
//
//    /**
//     * 增加根据params计算，入对应的分区队列
//     * @param topicName
//     * @param key
//     * @param value
//     * @param params
//     * @return
//     */
//    public static boolean push(String topicName, String key, String value, List<String> params) throws MQException {
//        String param = !CollectionUtils.isEmpty(params) ? transParam(params) : "";
//        return push(topicName,key,value,null,param);
//    }
//
//    /**
//     * 增加根据params计算，入对应的分区队列
//     * @param topicName
//     * @param key
//     * @param value
//     * @param tag
//     * @param params
//     * @return
//     */
//    public static boolean push(String topicName, String key, String value, String tag, String params) {
//        return  push(topicName,key,value,tag,null,params);
//    }
//
//    /**
//     * 增加根据params计算，入对应的分区队列
//     * @param topicName
//     * @param key
//     * @param value
//     * @param tag
//     * @param delayTimeLevel
//     * @param params
//     * @return
//     */
//    public static boolean push(String topicName, String key, String value, String tag, Integer delayTimeLevel, String params) {
//        if (!SendMessageJudgeUtil.isOpen) {
//            return false;
//        }
//        boolean flag = false;
//        int valLength = value.getBytes(StandardCharsets.UTF_8).length;
//        producer = getProducer();
//        if (!sizeEnable || valLength < maxSize) {
//            if (producer == null) {
//                log.warn("生产者对象还未创建, 请先创建对象");
//                flag = false;
//            } else {
//                // 该参数代表送往哪个队列名
//                MQMessage message = new MQMessage(topicName, key, tag, value.getBytes(),params);
//                if (delayTimeLevel != null) {
//                    message.setDelayTimeLevel(delayTimeLevel);
//                }
//                for(int i = 0;i < mqRetryNum + 1; i++){
//                    try {
//                        MQSendResult sendResult = producer.sendByGroupId(message);
//                        if (sendResult != null && sendResult.getSendStatus() == MQSendStatus.SEND_OK) {
//                            if (!"nti_api_data".equals(tag)) {
//                                log.info("入队成功, 主题名: {}, tag名: {}", topicName, tag);
//                            }
//                            flag = true;
//                            break;
//                        } else {
//                            log.warn("入队失败, 主题名: {}, tag名: {}, value: {}", topicName, tag, value);
//                            log.info("尝试重新入队, 主题名: {}", topicName);
//                            MQSendResult sendResult1 = producer.sendByGroupId(message);
//                            if (sendResult1 == null || sendResult1.getSendStatus() != MQSendStatus.SEND_OK) {
//                                log.info("重新入队失败");
//                                flag = false;
//                            } else {
//                                log.info("重新入队成功");
//                                flag = true;
//                                break;
//                            }
//                        }
//                    } catch (Exception e) {
//                        sendFailHandle();
//                        log.error("入队异常, 主题名: {} tag名: {},尝试第{}次重新入队,异常: {}",topicName,tag,i,e);
//                    }
//                    try {
//                        Thread.sleep(mqRetryDelay);
//                    } catch (InterruptedException e) {
//                        log.error("重新入队失败休眠异常",e);
//                    }
//                }
//            }
//            if (!flag) {
//                sendFailHandle();
//            }
//        } else {
//            flag = false;
//            log.info("消息入队异常，topicName：{}，tag:{}，消息体大小：{}，消息最大值:{}，消息丢弃。", topicName, tag, valLength, maxSize);
//        }
//        return flag;
//    }
//
//    public static String transParam(List<String> params){
//        StringBuffer buffer = new StringBuffer();
//        for (String param : params) {
//            buffer.append(param);
//        }
//        return buffer.toString();
//    }
//
//}
