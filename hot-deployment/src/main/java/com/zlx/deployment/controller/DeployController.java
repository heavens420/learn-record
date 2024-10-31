package com.zlx.deployment.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.zlx.deployment.bean.User;
import com.zlx.deployment.exposeInterface.Calculator;
import com.zlx.deployment.exposeInterfaceImpl.TestInjectBean;
import com.zlx.deployment.service.ExecutorService;
import com.zlx.deployment.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class DeployController {

    @Resource
    private ExecutorService executorService;

    @Resource
    private SyncService syncService;

    private Long index = 0L;

//
//    @Qualifier("hotDeployWithReflect")
//    @Autowired(required = false)
//    private Calculator calculator;
//
//    @Autowired(required = false)
//    private TestInjectBean testInjectBean;
//
//    @GetMapping("/add")
//    public String calculateOperation(int a,int b) {
//        int sum = calculator.add(a, b);
//        return String.valueOf(sum);
//    }
//
//    public String calculateOperation2(int a, int b) {
//        return null;
//    }
//
//    @GetMapping("/injectBean")
//    public String getTestInjectBean() {
//        return testInjectBean.printString();
//    }


    @Resource
    private Environment environment;

    @NacosValue(value = "${values:aaa}",autoRefreshed = true)
    private List<String> values;

    @NacosValue(value = "${cncc-fc-admin:ddd}",autoRefreshed = true)
    private String testValue;

    @GetMapping("/key")
    public String getProperty(String key) {
        String property = environment.getProperty(key);
        System.out.println("testValue:" + testValue);
        System.out.println("values:" + values);
        return property;
    }


    @PostMapping("/user")
    public User testWait(@RequestBody User user) throws InterruptedException {
        user.setBeginTime(LocalDateTime.now());
        user.setBeginName(Thread.currentThread().getName());
        user.setIndex(index++);
        executorService.addUser2Queue(user);
        User resUser = syncService.getUser(user);
        return resUser;
    }


    @PostMapping("/p")
    public Object testPost(@RequestBody User user) {
        System.out.println("name:" + user.getName());

        return user.toString();
    }

}
