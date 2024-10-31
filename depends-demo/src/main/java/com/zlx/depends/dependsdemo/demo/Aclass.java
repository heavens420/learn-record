package com.zlx.depends.dependsdemo.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@DependsOn("bclass")
@Component
@Slf4j
public class Aclass implements  Cclass{
    public Aclass() {
        log.info("Aclass is created");
    }

    @Resource
    private Bclass bclass;

    @PostConstruct
    public void print(){

        log.info("Aclass is running:{}",Bclass.CC);
    }


    @Override
    public void sout() {
        String v1 = bclass.getV1();
        log.info("Cclass is running :{}", v1);
    }
}
