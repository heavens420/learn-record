package com.zlx.depends.dependsdemo.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
@ConfigurationProperties(prefix = "vv")
public class Bclass {
    public Bclass() {
        log.info("Bclass is created");
    }

    private String v1;

    @Value("${bb}")
    private String bb;

    @Autowired
    private Environment environment;

    public static String CC;

    public String getV1() {
        return v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }

    @PostConstruct
    public void print() {
        String aa = environment.getProperty("aa");
        String cc = environment.getProperty("cc");
        log.info("cc property is: {}", cc);
        CC = cc;
        log.info("Bclass is running: aa={},bb={},v1={}", aa,bb,v1);
    }
}
