package com.zlx.deployment.service;


import com.zlx.deployment.bean.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SyncService {

    public User getUser(User user) throws InterruptedException {
        synchronized (user) {
            user.wait(10000);
            user.setEndTime(LocalDateTime.now());
        }
        return user;
    }
}
