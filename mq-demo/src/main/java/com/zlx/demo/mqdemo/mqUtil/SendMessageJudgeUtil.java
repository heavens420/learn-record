//package com.zlx.demo.mqdemo.mqUtil;
//
//import com.alibaba.fastjson.JSONObject;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.Date;
//import java.util.Map;
//
///**
// * 判断是否入MQ
// */
//
//@Slf4j
//@Data
//@Component
//public class SendMessageJudgeUtil implements Runnable {
//    /**
//     * 告警type
//     */
//    public final static Integer MQ_MONITOR_ALARM_CODE = 500007;
//
//    @Resource
//    private CnccCache cnccCache;
//
//    @Resource
//    private KafkaUtil kafkaUtil;
//
//    @Resource
//    private CnccTopic cnccTopic;
//
//    @Resource
//    private MqConfigValues mqConfigValues;
//
//    @Resource
//    private RocketMqProducer rocketMqProducer;
//
//    //阈值，默认0，不开启此功能
//    @Value(value = "${cncc.mq.switch.threshold:0}")
//    private long threshold;
//
//    @Value(value = "${mqRetryNum:1}")
//    private int mqRetryNum;
//
//    //阈值有效时间，单位秒
//    @Value(value = "${cncc.mq.cache.expire.time:300}")
//    private int expireTime;
//
//    //超过阈值是否自动关闭？1-会自动关闭/0-不会关闭（默认）/2-迁移到备用MQ组件
//    @Value(value = "${cncc.mq.switch.errorStrategy:0}")
//    private String errorStrategy;
//    @Value("${cncc.kafka.enable_cncc:false}")
//    private Boolean enable_cncc;
//    public static boolean isOpen = true;
//
//    private static final String MQ_SWITCH_KEY = "mq_threshold";
//
//    //标记MQ的关闭是否是由页面人为操作的，如果是人为的，则不会自动开启
//    private static final String MQ_STATUS_ARTIFICIAL_KEY = "mq_status_artificial";
//    private static final int MQ_STATUS_ARTIFICIAL_EXPTIME = 3600;
//
//
//    private static final String MQ_SWITCH_OPEN = "1";
//    private static final String MQ_SWITCH_CLOSE = "0";
//
//
//    @Override
//    public void run() {
//        char checkCount = 10;
//        if(cnccCache.get(MQ_STATUS_ARTIFICIAL_KEY) != null){
//            //有人为关闭标记，不能自动恢复MQ开关
//            //标记续期
//            cnccCache.expire(MQ_STATUS_ARTIFICIAL_KEY,MQ_STATUS_ARTIFICIAL_EXPTIME);
//            //启动时设置开关状态，避免重启后关闭状态下能入mq
//            isOpen = false;
//        }
//        while (true) {
//            checkCount ++;
//            try {
//                Long incr = cnccCache.incr(MQ_SWITCH_KEY, 0L);
//                if(checkCount % 5 == 0) {
//                    //50秒输出一次监控信息到日志，避免日志泛滥
//                    log.info("全局MQ异常次数监控，当前异常次数：{}/{}", incr,threshold);
//                    checkCount -= 5;
//                }
//                if(threshold == 0) {
//                   //关闭状态不做处理，防止因共用同一个cache的模块开启导致本模块的异常操作
//                }else if (incr >= threshold && isOpen) {
//                    if("1".equals(errorStrategy)) {
//                        isOpen = false;
//                        log.warn("全局MQ异常次数超过阈值{}，暂时关闭消息推送",threshold);
//                    }else if("2".equals(errorStrategy)) {
//                        //TODO 迁移MQ组件，迁移后需要避免来回迁移
//                    }else{
//                        if(checkCount % 5 == 0){
//                            log.warn("全局MQ异常次数超过阈值{}，但未配置自动关闭",threshold);
//                        }
//                    }
//                } else if ( incr < threshold && !isOpen ) {
//                    if(cnccCache.get(MQ_STATUS_ARTIFICIAL_KEY) != null){
//                        //有人为关闭标记，不能自动恢复MQ开关
//                        cnccCache.expire(MQ_STATUS_ARTIFICIAL_KEY,MQ_STATUS_ARTIFICIAL_EXPTIME);//标记续期
//                    }else{
//                        isOpen = true;
//                    }
//                }
//            } catch (Exception e) {
//                log.error("MQ异常阈值监控异常", e);
//            } finally {
//                try {
//                    Thread.sleep(10 * 1000L);
//                } catch (InterruptedException e) {
//                    log.error("MQ异常阈值监控线程休眠异常", e);
//                }
//            }
//        }
//    }
//
//    /**
//     * MQ生产者推送消息异常到kafka，
//     * 会检查kafka是否可用
//     * 同时可能会关闭MQ
//     */
//    void exceptionHandle() {
//        Long ckCount = 0L;
//        if(mqRetryNum == 1){
//            if (threshold == 0) {
//                return;
//            }else {
//                ckCount = threshold;
//            }
//        }else {
//            ckCount = Long.valueOf(mqRetryNum);
//        }
//        Long count = cnccCache.incr(MQ_SWITCH_KEY, expireTime);
//        if (count == ckCount) {
//            //异常量达到阈值，关闭MQ生产者的使用，产生告警
//            if("1".equals(errorStrategy)){
//                log.warn("当前POD消息队列生产者推送消息异常量达到阈值{}/{}秒, 暂时关闭消息推送!",count,expireTime);
//                isOpen = false;
//            }else if("2".equals(errorStrategy)) {
//                //TODO 迁移MQ组件，迁移后需要避免来回迁移
//
//            }else{
//                log.warn("当前POD消息队列生产者推送消息异常量达到阈值{}/{}秒, 但不关闭消息推送!",count,expireTime);
//            }
//            if (enable_cncc) {
//                JSONObject object = new JSONObject();
//                object.put("alarmType", MQ_MONITOR_ALARM_CODE);
//                object.put("alarmTitle", "MQ生产者消息推送异常");
//                object.put("alarmContent", "控制框架消息队列生产者异常，推送消息失败次数达到阈值: " + ckCount + "次!");
//                object.put("alarmTime", new Date());
//                if (mqConfigValues.isCtgMq()) {
//                    JSONObject ctgMq = new JSONObject();
//                    ctgMq.put("producerGroupName", mqConfigValues.getProducerGroupName());
//                    ctgMq.put("addr", mqConfigValues.getAddr());
//                    ctgMq.put("authId", mqConfigValues.getAuthId());
//                    ctgMq.put("authPwd", mqConfigValues.getAuthPwd());
//                    ctgMq.put("tenantId", mqConfigValues.getTenantId());
//                    ctgMq.put("clusterName", mqConfigValues.getClusterName());
//                    ctgMq.put("vipChannelEnabled", mqConfigValues.getVipChannelEnabled());
//                    object.put("alarmObject", ctgMq);
//                } else if (mqConfigValues.isRocketMq()) {
//                    JSONObject rocketMq = new JSONObject();
//                    rocketMq.put("producerGroupName", rocketMqProducer.getGroupName());
//                    rocketMq.put("namesrvAddr", mqConfigValues.getAddr());
//                    object.put("alarmObject", rocketMq);
//                }
//
//                object.put("alarmLevel", 1);
//                object.put("alarmProvider", "控制框架");
//                object.put("clearStatus", 0);
//                object.put("alarmReason", "控制框架消息队列生产者推送消息异常次数达到阈值: " + ckCount + ", 立即关闭消息队列" +
//                        "的使用, 这将会导致部分工单信息的丢失!");
//                kafkaUtil.sendErrorMsgToKafka(cnccTopic.getNeAlarm(), object.toJSONString());
//            }
//        } else if (count > ckCount && isOpen) {
//            if("1".equals(errorStrategy)) {
//                isOpen = false;
//                log.warn("当前POD消息队列生产者推送消息异常量{}超过阈值{}/{}秒, 暂时关闭消息推送!",count,ckCount,expireTime);
//            }else {
//                log.warn("当前POD消息队列生产者推送消息异常量{}超过阈值{}/{}秒, 但不关闭消息推送!",count,ckCount,expireTime);
//            }
//
//        }else {
//            log.warn("当前POD消息队列生产者推送消息异常量{}/{}秒",count,expireTime);
//        }
//    }
//
//    /**
//     * 页面上手动关闭MQ
//     * @param param
//     */
//    public void mqSwitchControl(Map<String, Object> param) {
//        String tag = String.valueOf(param.get("tag"));
//        if (MQ_SWITCH_OPEN.equals(tag) && !isOpen) {
//            log.info("手动开启MQ");
//            //页面通知开启
//            isOpen = true;
//            cnccCache.del(MQ_SWITCH_KEY);
//            cnccCache.del(MQ_STATUS_ARTIFICIAL_KEY); //删除手动关闭标记
//        } else if (MQ_SWITCH_CLOSE.equals(tag) && isOpen) {
//            log.info("手动关闭MQ");
//            //页面通知关闭
//            isOpen = false;
//            Long incr = cnccCache.incr(MQ_SWITCH_KEY, 0L);
//            if (threshold > incr) {
//                cnccCache.incr(MQ_SWITCH_KEY, threshold - incr);
//                cnccCache.expire(MQ_SWITCH_KEY, expireTime);
//            }
//            //添加手动关闭标记，标记初始持续时间~
//            cnccCache.set(MQ_STATUS_ARTIFICIAL_KEY,true,MQ_STATUS_ARTIFICIAL_EXPTIME);
//        }else {
//            log.info("手动配置MQ开关状态无效:{}-{}",tag,isOpen);
//        }
//    }
//
//    public String getMqStatus() {
//        String flag;
//        Object artificialKey = cnccCache.get(MQ_STATUS_ARTIFICIAL_KEY);
//        if(artificialKey != null){ //人为关闭了
//            flag = "0";
//        }else{
//            if (threshold == 0) {
//                flag = "1";
//            } else {
//                Long incr = cnccCache.incr(MQ_SWITCH_KEY, 0L);
//                if(artificialKey != null){ //人为关闭了
//                    flag = "0";
//                }else{
//                    if (incr < threshold) {
//                        flag = "1";
//                    } else {
//                        flag = "0";
//                    }
//                }
//            }
//        }
//        return flag;
//    }
//}
