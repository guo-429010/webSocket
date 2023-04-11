package com.example.ws.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/myWs")
@Component
@Slf4j
public class WsServerEndpont {

    static Map<String,Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session){
        sessionMap.put(session.getId(),session);
        log.info("websocket已连接");
    }

    @OnMessage
    public String onMessage(String msg){
        log.info("收到消息：" + msg);
        return "收到";
    }

    @OnClose
    public void onClose(Session session){
        sessionMap.remove(session.getId());
        log.info("websocket已断开");
    }

}
