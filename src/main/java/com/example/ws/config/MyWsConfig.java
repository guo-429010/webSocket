package com.example.ws.config;

import com.example.ws.intercept.MyWsHandler;
import com.example.ws.intercept.MyWsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

@Configuration
@EnableWebSocket
public class MyWsConfig implements WebSocketConfigurer {

    @Resource
    MyWsHandler myWsHandler;

    @Resource
    MyWsInterceptor myWsInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWsHandler, "/myWs1")
                .addInterceptors(myWsInterceptor)
                .setAllowedOrigins("*");
    }
}
