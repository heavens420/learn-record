package com.zlx.demo.mqdemo.mqUtil;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @Description 用于加载集团封装MQ的配置项 ctgMQ
 */
@Component("MqConfigValues")
@Validated
@Data
public class MqConfigValues {
    public final static String ROCKET_MQ_STR = "rocketmq";
    public final static String CTG_MQ_STR = "ctgmq";


    /**
     * 此开关用于判断是否开启MQ功能，但是无法通过nacos动态更新
     * 能使用nacos的模块应尽量使用cncc.mq.enable配置项作为开关判断依据
     */
    @Value("${cncc.rocketmq.enable:false}")
    private Boolean enable;

    @Value(value = "${mqRetryNum:1}")
    private int mqRetryNum;

    @Value(value = "${mqRetryDelay:0}")
    private Long mqRetryDelay;

    /**
     * 生产者组名
     */
    @Value("${cncc.rocketmq.producerGroupName:P_CNCC_SC_PRODUCER}")
    private String producerGroupName;

    /**
     * 消费者名key
     */
    @Value("${cncc.rocketmq.customerNameKey_cmdInstance:YF_C_T_NEOP_CMD_INSTANCE}")
    private String customerNameKey_cmdInstance;

    /**
     * 消费者名key
     */
    @Value("${cncc.rocketmq.customerNameKey_ckeckEws:YF_C_T_CHECK_EWS}")
    private String customerNameKey_ckeckEws;

    /**
     * 消费者名key
     */
    @Value("${cncc.rocketmq.customerNameKey_ckeckEwsXml:YF_C_T_CHECK_EWS_XML}")
    private String customerNameKey_ckeckEwsXml;

    @Value("${cncc.rocketmq.customerNameKey_ckeckEwsUpdate:YF_C_T_CHECK_EWS_UPDATE}")
    private String customerNameKey_ckeckEwsUpdate;

    @Value("${cncc.rocketmq.customerNameKey_cmdInstanceUpdate:YF_C_T_NEOP_CMD_INSTANCE_UPDATE}")
    private String customerNameKey_cmdInstanceUpdate;
    @Value("${cncc.rocketmq.customerNameKey_neopConfig:YF_C_T_NEOP_CONFIG}")
    private String customerNameKey_neopConfig;
    @Value("${cncc.rocketmq.customerNameKey_neopCallBackXml:YF_C_T_NEOP_CALL_BACK_XML}")
    private String customerNameKey_neopCallBackXml;
    @Value("${cncc.rocketmq.customerNameKey_asyncEws:YF_C_T_NEOP_ASYNEWS}")
    private String customerNameKey_asyncEws;

    @Value("${cncc.rocketmq.customerNameKey_serverAsyncReply:YF_C_T_NEOP_ASYNEWS}")
    private String customerNameKey_serverAsyncReply;

    @Value("${cncc.rocketmq.customerNameKey_tfj_recovery:C_T_TFJ_RECOVERY_EWS}")
    private String customerNameKey_tfj_recovery;

    @Value("${cncc.rocketmq.customerNameKey_tfj_stop:C_T_TFJ_STOP_EWS}")
    private String customerNameKey_tfj_stop;

    /**
     * 地址列表 ip:port;ip:port;ip:port
     */
    @Value("${cncc.rocketmq.addr:''}")
    public String addr;

    /**
     * 用户
     */
    @Value("${cncc.rocketmq.authId:cncc-sc-ah}")
    public String authId;

    /**
     * 密码
     */
    @Value("${cncc.rocketmq.authPwd:cncc-sc-ah}")
    public String authPwd;

    /**
     * 租户id
     */
    @Value("${cncc.rocketmq.tenant-id:1}")
    private String tenantId;

    /**
     * 集群名称
     */
    @Value("${cncc.rocketmq.clusterName:cncc_test}")
    public String clusterName;

    /**
     * 最小消费线程
     */
    @Value("${cncc.rocketmq.consumer-thread-min:10}")
    private String consumerThreadMin;

    /**
     * 最大消费线程
     */
    @Value("${cncc.rocketmq.consumer-thread-max:20}")
    private String consumerThreadMax;

    @Value("${cncc.rocketmq.vipChannelEnabled:false}")
    private Boolean vipChannelEnabled;

    @Value("${cncc.rocketmq.sendTimeout:3000}")
    private String SendMsgTimeout;


    @Value("${cncc.rocketmq.type:rocketmq}")
    private String type;

    @Value("${cncc.rocketmq.maxMessageSize:4096}")
    private Integer maxMessageSize;

    @Value("${cncc.rocketmq.aclEnable:true}")
    private Boolean aclEnable;

    @Value("${cncc.rocketmq.accessKey:'rocketmq2'}")
    private String accessKey;

    @Value("${cncc.rocketmq.secretKey:'000cKadmin@#2024'}")
    private String secretKey;

    @Value("${cncc.rocketmq.retryTimesWhenSendFailed:2}")
    private Integer retryTimesWhenSendFailed;

    @Value("${cncc.rocketmq.consumeMessageBatchMaxSize:1}")
    private Integer consumerMaxSize;


    @Value("${cncc.rocketmq.producerNumber:5}")
    private int producerNumber;

    @Value("${cncc.rocketmq.customerNameKey_cmdInstanceUpdateRetry:cmdInstanceUpdateRetry_consumer}")
    private String cmdInstanceURetryGroupName;

    @Value("${cncc.rocketmq.customerNameKey_ckeckEwsUpdateRetry:ckeckEwsUpdateRetry_consumer}")
    private String ckeckEwsURetryGroupName;

    @Value("${cncc.rocketmq.customerNameKey_ckeckEwsUpdateRetry:YF_C_T_CHECK_EWS_UPDATE_RETRY}")
    private String customerNameKey_ckeckEwsUpdateRetry;

    @Value("${cncc.rocketmq.customerNameKey_cmdInstanceUpdateRetry:YF_C_T_NEOP_CMD_INSTANCE_UPDATE_RETRY}")
    private String customerNameKey_cmdInstanceUpdateRetry;

    @Value("${cncc.rocketmq.customerNameKey_neopNeConnectFailRetry:YF_C_T_NEOP_NE_CONNECT_FAIL_RETRY}")
    private String customerNameKey_neopNeConnectFailRetry;

    @Value("${cncc.rocketmq.customerNameKeyAsyncRes:YF_C_T_CNCC_ASYNC_RES}")
    private String customerNameKeyAsyncRes;


}
