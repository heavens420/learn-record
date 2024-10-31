package com.zlx.demo.tokenbucket.controller;


import com.zlx.demo.tokenbucket.service.DoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class RequestController {

    @Autowired
    private DoRequest doRequest;
    private int count;

    @GetMapping("/limit")
    public void sendRequest(String id, int interval) throws InterruptedException {
        while (true) {
            boolean allowed = doRequest.isAllowed(id);
            count++;
            if (allowed) {
                log.info("access===={}=====Request:{}", id, count);
            } else {
                log.info("deny===={}=====Request:{}", id, count);
            }
            TimeUnit.SECONDS.sleep(interval);
        }
    }
}
