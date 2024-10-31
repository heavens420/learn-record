package com.zlx.demo.mqdemo.mqUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @Description 用于加载集团封装MQ的配置项 ctgMQ
 */
@Component("MqConfigValues")
@Validated
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


    @Value("${cncc.rocketmq.type:ctgmq}")
    private String type;

    @Value("${cncc.rocketmq.maxMessageSize:4096}")
    private Integer maxMessageSize;

    @Value("${cncc.rocketmq.aclEnable:false}")
    private Boolean aclEnable;

    @Value("${cncc.rocketmq.accessKey:''}")
    private String accessKey;

    @Value("${cncc.rocketmq.secretKey:''}")
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

    public String getCustomerNameKeyAsyncRes() {
        return customerNameKeyAsyncRes;
    }

    public void setCustomerNameKeyAsyncRes(String customerNameKeyAsyncRes) {
        this.customerNameKeyAsyncRes = customerNameKeyAsyncRes;
    }

    public String getCustomerNameKey_neopNeConnectFailRetry() {
        return customerNameKey_neopNeConnectFailRetry;
    }

    public void setCustomerNameKey_neopNeConnectFailRetry(String customerNameKey_neopNeConnectFailRetry) {
        this.customerNameKey_neopNeConnectFailRetry = customerNameKey_neopNeConnectFailRetry;
    }

    public int getProducerNumber() {
        return producerNumber;
    }

    public void setProducerNumber(int producerNumber) {
        this.producerNumber = producerNumber;
    }

    public String getSendMsgTimeout() {
        return SendMsgTimeout;
    }

    public void setSendMsgTimeout(String sendMsgTimeout) {
        SendMsgTimeout = sendMsgTimeout;
    }

    public String getProducerGroupName() {
        return producerGroupName;
    }

    public void setProducerGroupName(String producerGroupName) {
        this.producerGroupName = producerGroupName;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getAuthPwd() {
        return authPwd;
    }

    public void setAuthPwd(String authPwd) {
        this.authPwd = authPwd;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getCustomerNameKey_cmdInstance() {
        return customerNameKey_cmdInstance;
    }

    public void setCustomerNameKey_cmdInstance(String customerNameKey_cmdInstance) {
        this.customerNameKey_cmdInstance = customerNameKey_cmdInstance;
    }

    public String getCustomerNameKey_ckeckEws() {
        return customerNameKey_ckeckEws;
    }

    public void setCustomerNameKey_ckeckEws(String customerNameKey_ckeckEws) {
        this.customerNameKey_ckeckEws = customerNameKey_ckeckEws;
    }

    public String getCustomerNameKey_ckeckEwsXml() {
        return customerNameKey_ckeckEwsXml;
    }

    public void setCustomerNameKey_ckeckEwsXml(String customerNameKey_ckeckEwsXml) {
        this.customerNameKey_ckeckEwsXml = customerNameKey_ckeckEwsXml;
    }

    public String getCustomerNameKey_ckeckEwsUpdate() {
        return customerNameKey_ckeckEwsUpdate;
    }

    public void setCustomerNameKey_ckeckEwsUpdate(String customerNameKey_ckeckEwsUpdate) {
        this.customerNameKey_ckeckEwsUpdate = customerNameKey_ckeckEwsUpdate;
    }

    public String getCustomerNameKey_cmdInstanceUpdate() {
        return customerNameKey_cmdInstanceUpdate;
    }

    public void setCustomerNameKey_cmdInstanceUpdate(String customerNameKey_cmdInstanceUpdate) {
        this.customerNameKey_cmdInstanceUpdate = customerNameKey_cmdInstanceUpdate;
    }

    public String getCustomerNameKey_neopConfig() {
        return customerNameKey_neopConfig;
    }

    public void setCustomerNameKey_neopConfig(String customerNameKey_neopConfig) {
        this.customerNameKey_neopConfig = customerNameKey_neopConfig;
    }

    public String getCustomerNameKey_neopCallBackXml() {
        return customerNameKey_neopCallBackXml;
    }

    public void setCustomerNameKey_neopCallBackXml(String customerNameKey_neopCallBackXml) {
        this.customerNameKey_neopCallBackXml = customerNameKey_neopCallBackXml;
    }

    public String getCustomerNameKey_asyncEws() {
        return customerNameKey_asyncEws;
    }

    public void setCustomerNameKey_asyncEws(String customerNameKey_asyncEws) {
        this.customerNameKey_asyncEws = customerNameKey_asyncEws;
    }

    public String getConsumerThreadMin() {
        return consumerThreadMin;
    }

    public void setConsumerThreadMin(String consumerThreadMin) {
        this.consumerThreadMin = consumerThreadMin;
    }

    public String getConsumerThreadMax() {
        return consumerThreadMax;
    }

    public void setConsumerThreadMax(String consumerThreadMax) {
        this.consumerThreadMax = consumerThreadMax;
    }

    public String getCustomerNameKey_serverAsyncReply() {
        return customerNameKey_serverAsyncReply;
    }

    public void setCustomerNameKey_serverAsyncReply(String customerNameKey_serverAsyncReply) {
        this.customerNameKey_serverAsyncReply = customerNameKey_serverAsyncReply;
    }

    public Boolean getVipChannelEnabled() {
        return vipChannelEnabled;
    }

    public void setVipChannelEnabled(Boolean vipChannelEnabled) {
        this.vipChannelEnabled = vipChannelEnabled;
    }

    public String getCustomerNameKey_tfj_recovery() {
        return customerNameKey_tfj_recovery;
    }

    public void setCustomerNameKey_tfj_recovery(String customerNameKey_tfj_recovery) {
        this.customerNameKey_tfj_recovery = customerNameKey_tfj_recovery;
    }

    public String getCustomerNameKey_tfj_stop() {
        return customerNameKey_tfj_stop;
    }

    public void setCustomerNameKey_tfj_stop(String customerNameKey_tfj_stop) {
        this.customerNameKey_tfj_stop = customerNameKey_tfj_stop;
    }

    public Integer getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(Integer maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public Boolean getAclEnable() {
        return aclEnable;
    }

    public void setAclEnable(Boolean aclEnable) {
        this.aclEnable = aclEnable;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getRetryTimesWhenSendFailed() {
        return retryTimesWhenSendFailed;
    }

    public void setRetryTimesWhenSendFailed(Integer retryTimesWhenSendFailed) {
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
    }

    public Integer getConsumerMaxSize() {
        return consumerMaxSize;
    }

    public void setConsumerMaxSize(Integer consumerMaxSize) {
        this.consumerMaxSize = consumerMaxSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCtgMq() {
        return CTG_MQ_STR.equals(getType());
    }
    public boolean isRocketMq() {
        return ROCKET_MQ_STR.equals(getType());
    }

    @Deprecated
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getCmdInstanceURetryGroupName() {
        return cmdInstanceURetryGroupName;
    }

    public void setCmdInstanceURetryGroupName(String cmdInstanceURetryGroupName) {
        this.cmdInstanceURetryGroupName = cmdInstanceURetryGroupName;
    }

    public String getCkeckEwsURetryGroupName() {
        return ckeckEwsURetryGroupName;
    }

    public void setCkeckEwsURetryGroupName(String ckeckEwsURetryGroupName) {
        this.ckeckEwsURetryGroupName = ckeckEwsURetryGroupName;
    }

    public String getCustomerNameKey_ckeckEwsUpdateRetry() {
        return customerNameKey_ckeckEwsUpdateRetry;
    }

    public void setCustomerNameKey_ckeckEwsUpdateRetry(String customerNameKey_ckeckEwsUpdateRetry) {
        this.customerNameKey_ckeckEwsUpdateRetry = customerNameKey_ckeckEwsUpdateRetry;
    }

    public String getCustomerNameKey_cmdInstanceUpdateRetry() {
        return customerNameKey_cmdInstanceUpdateRetry;
    }

    public void setCustomerNameKey_cmdInstanceUpdateRetry(String customerNameKey_cmdInstanceUpdateRetry) {
        this.customerNameKey_cmdInstanceUpdateRetry = customerNameKey_cmdInstanceUpdateRetry;
    }

    public int getMqRetryNum() {
        return mqRetryNum;
    }

    public void setMqRetryNum(int mqRetryNum) {
        this.mqRetryNum = mqRetryNum;
    }

    public Long getMqRetryDelay() {
        return mqRetryDelay;
    }

    public void setMqRetryDelay(Long mqRetryDelay) {
        this.mqRetryDelay = mqRetryDelay;
    }
}
