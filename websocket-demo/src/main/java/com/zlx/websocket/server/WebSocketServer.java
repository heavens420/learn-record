package com.zlx.websocket.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * WebSocket服务实现类
 *
 */
@Slf4j
@Component
@ServerEndpoint(value = "/ws/talk")
public class WebSocketServer {

    /**
     * 建立连接的方法
     * 如果有需要在建立连接的时候做一些事情，可以在这个方法里面写
     * @param session
     */
    @OnOpen
    public void onOpen(Session session){
        log.info("客户端建立连接:{}", session.getId());
    }

    /**
     * 消息接收的方法
     * 当客户端发送消息的时候，会自动调用这个方法
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("客户端{}消息:{}", session.getId(), message);
        if ("Google地址是什么".equals(message)) {
            sendMessage(session, "https://www.google.com");
            return;
        }
        if ("油管地址是什么".equals(message)) {
            sendMessage(session, "点击访问：<a style=\"color: #007aff\" href=\"https://www.youtube.com\" target=\"_blank\">https://www.youtube.com</a>");
            return;
        }
        if ("chatgpt地址是什么".equals(message)) {
            sendMessage(session, "你去问chatgpt");
            return;
        }
        if ("你知道什么".equals(message)) {
            sendMessage(session, "你猜我知道什么");
            return;
        }
        // 万能回复
        if (message.contains("吗？") || message.contains("吗?")) {
            if (message.startsWith("你")) {
                message = message.replaceAll("你", "我");
            }
            message = message.replaceAll("吗？", "！");
            message = message.replaceAll("吗\\?", "!");
            sendMessage(session, message);
            return;
        }
        sendMessage(session, "抱歉 我是人工智障");
    }

    /**
     * 关闭连接的方法
     * 当客户端关闭连接的时候，会自动调用这个方法
     * @param session
     */
    @OnClose
    public void onclose(Session session){
        log.info("客户端关闭连接:{}", session.getId());
    }

    /**
     * 连接异常时的方法，必须包含参数Throwable
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        log.error("连接异常:{}", error.getMessage());
    }

    /**
     * 发送消息的方法
     * @param session
     * @param message
     */
    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
