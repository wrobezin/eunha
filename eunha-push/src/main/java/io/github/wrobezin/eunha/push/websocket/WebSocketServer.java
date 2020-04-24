package io.github.wrobezin.eunha.push.websocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/24 14:37
 */
@ServerEndpoint("/push/ws/{userId}")
@Component
@Slf4j
public class WebSocketServer {
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    private Session session;
    private String userId = "";

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        webSocketMap.remove(userId);
        webSocketMap.put(userId, this);
    }

    @OnClose
    public void onClose() {
        webSocketMap.remove(userId);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到WebSocket消息：{}", message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket出错", error);
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendToOne(String message, @PathParam("userId") String userId) throws IOException {
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        }
    }

    public static void sendToAll(String message) {
        webSocketMap.values().forEach(server -> {
            try {
                server.sendMessage(message);
            } catch (IOException e) {
                log.error("发送WebSocket消息出错", e);
            }
        });
    }
}
