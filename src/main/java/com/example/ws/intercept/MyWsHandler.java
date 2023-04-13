package com.example.ws.intercept;

import com.example.ws.entity.SessionBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * webSocket主程序
 */
@Slf4j
@Component
public class MyWsHandler extends AbstractWebSocketHandler {

    private static Map<String, SessionBean> sessionBeanMap;
    private static AtomicInteger clientIdMaker;
    private static StringBuffer stringBuffer;
    static {
        sessionBeanMap = new ConcurrentHashMap<>();
        clientIdMaker = new AtomicInteger(0);
        stringBuffer = new StringBuffer();
    }

    //链接建立
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        SessionBean sessionBean = new SessionBean(session,clientIdMaker.getAndIncrement());
        sessionBeanMap.put(session.getId(),sessionBean);
        log.info(sessionBeanMap.get(session.getId()).getClientId() + "建立了链接");
        stringBuffer.append(sessionBeanMap.get(session.getId()).getClientId() + "进入了群聊<br/>");
        sendMessage(sessionBeanMap);
    }

    //收到消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        log.info(sessionBeanMap.get(session.getId()).getClientId() + ":" + message.getPayload());
        stringBuffer.append(sessionBeanMap.get(session.getId()).getClientId() + ":" + message.getPayload() + "<br/>");
        sendMessage(sessionBeanMap);
    }

    //传输异常
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        if(session.isOpen()){
            session.close();
        }
        sessionBeanMap.remove(session.getId());
    }

    //链接关闭
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        int clientId = sessionBeanMap.get(session.getId()).getClientId();
        sessionBeanMap.remove(session);
        log.info(clientId + "关闭了链接");
        stringBuffer.append(clientId + "退出了群聊<br/>");
        sendMessage(sessionBeanMap);
    }

//    //每2秒发送一次心跳
//    @Scheduled(fixedRate = 2000)
//    public void sendMsg() throws IOException {
//        for (String key:sessionBeanMap.keySet()){
//            sessionBeanMap.get(key).getWebSocketSession().sendMessage(new TextMessage("心跳"));
//        }
//    }

    public void sendMessage(Map<String,SessionBean> sessionBeanMap) {
        for(String key:sessionBeanMap.keySet()){
            try {
                sessionBeanMap.get(key).getWebSocketSession().sendMessage(new TextMessage(stringBuffer.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }
}
