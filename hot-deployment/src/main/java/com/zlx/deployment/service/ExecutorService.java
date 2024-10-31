package com.zlx.deployment.service;


import com.zlx.deployment.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@Order(10000)
@Slf4j
public class ExecutorService implements CommandLineRunner {

    BlockingQueue<User> blockingDeque = new ArrayBlockingQueue<User>(1000);

    @Override
    public void run(String... args) {
        log.info("------------ExecutorService begin-----------");
        try {
            while (true) {
                User user = blockingDeque.poll(1000, TimeUnit.SECONDS);
                log.info("userQueue:{}", blockingDeque.size());
                if (user != null) {
                    synchronized (user) {
                        user.setName(Thread.currentThread().getName() + LocalDateTime.now());
                        TimeUnit.SECONDS.sleep(1);
                        user.notifyAll();
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser2Queue(User user) throws InterruptedException {
        blockingDeque.offer(user, 100, TimeUnit.SECONDS);
    }
}
