package com.zlx.deployment.util;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.zlx.deployment.config.HotDeployWithSpring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.concurrent.Executor;


@Slf4j
public class SpringAnnotationUtils {

    /**
     * 判断一个类是否有 Spring 核心注解
     *
     * @param clazz 要检查的类
     * @return true 如果该类上添加了相应的 Spring 注解；否则返回 false
     */
    public static boolean hasSpringAnnotation(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        //是否是接口
        if (clazz.isInterface()) {
            return false;
        }
        //是否是抽象类
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }

        try {
            if (clazz.getAnnotation(Component.class) != null ||
                    clazz.getAnnotation(Repository.class) != null ||
                    clazz.getAnnotation(Service.class) != null ||
                    clazz.getAnnotation(Controller.class) != null ||
                    clazz.getAnnotation(Configuration.class) != null) {
                return true;
            }
        } catch (Exception e) {
            log.error("出现异常：{}", e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) throws NacosException, InterruptedException {
        String serverAddr = "localhost";
        String dataId = "test";
        String group = "test";
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);
        String content = configService.getConfig(dataId, group, 5000);
        System.out.println(content);
        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("receive:" + configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });

        boolean isPublishOk = configService.publishConfig(dataId, group, "content");
        System.out.println(isPublishOk);

        Thread.sleep(3000);
        content = configService.getConfig(dataId, group, 5000);
        System.out.println(content);

        boolean isRemoveOk = configService.removeConfig(dataId, group);
        System.out.println(isRemoveOk);
        Thread.sleep(3000);

        content = configService.getConfig(dataId, group, 5000);
        System.out.println(content);
        Thread.sleep(300000);

    }

}
