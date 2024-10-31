package com.zlx.demo.tokenbucket.service;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FunnelLimit {
    public static void doLimit() {
        //桶，用阻塞队列实现，容量为3
        final LinkedBlockingQueue<Integer> que = new LinkedBlockingQueue<>(3);

        //定时器，相当于服务的窗口，2s处理一个
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int v = que.poll();
                System.out.println("处理：" + v + "-" + LocalDateTime.now());
            }
        }, 2000, 2000, TimeUnit.MILLISECONDS);

        //无数个请求，i 可以理解为请求的编号
        int i = 0;
        while (true) {
            i++;
            try {
//                System.out.println("put:" + i);
                //如果是put，会一直等待桶中有空闲位置，不会丢弃
                que.put(i);
                //等待1s如果进不了桶，就溢出丢弃
//                que.offer(i, 1000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        doLimit();
        String ss = "aff\r\n" +
                "ddd";
        System.out.println("line:"+ System.lineSeparator());
        String[] split = ss.split(System.lineSeparator());
        for (String s : split) {
            System.out.println(s);
        }
    }
}
