package com.qlcvht.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketServer extends WebSocketServer {

    // Map conn -> userId
    private final Map<WebSocket, String> connUserMap = new ConcurrentHashMap<>();
    // Map userId -> conn
    private final Map<String, WebSocket> userConnMap = new ConcurrentHashMap<>();

    public ChatWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    public ChatWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("[WebSocket Server] Client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String userId = connUserMap.remove(conn);
        if (userId != null) {
            userConnMap.remove(userId);
            System.out.println("[WebSocket Server] User disconnected: " + userId);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String rawMessage) {
        if (rawMessage == null || rawMessage.trim().isEmpty()) return;

        // Message could be REGISTER:userId or ChatMessage payload
        if (rawMessage.startsWith("REGISTER:")) {
            String userId = rawMessage.substring("REGISTER:".length()).trim();
            connUserMap.put(conn, userId);
            userConnMap.put(userId, conn);
            System.out.println("[WebSocket Server] Registered user: " + userId);
            conn.send("REGISTER_OK:" + userId);
            return;
        }

        ChatMessage msg = ChatMessage.deserialize(rawMessage);
        if (msg != null) {
            System.out.println("[WebSocket Server] Routing msg from [" + msg.getFromId() + "] to [" + msg.getToId() + "]: " + msg.getTitle());
            
            // Send to target user if connected
            if (msg.getToId() != null) {
                WebSocket targetConn = userConnMap.get(msg.getToId());
                if (targetConn != null && targetConn.isOpen()) {
                    targetConn.send(rawMessage);
                }
            }

            // Also broadcast to all advisors if target is "ALL_ADVISORS" or advisor role
            if ("ALL_ADVISORS".equalsIgnoreCase(msg.getToId()) || "CO_VAN".equalsIgnoreCase(msg.getToId())) {
                for (Map.Entry<String, WebSocket> entry : userConnMap.entrySet()) {
                    if (entry.getKey().startsWith("CV") || entry.getKey().startsWith("cv_") || "admin".equals(entry.getKey())) {
                        if (entry.getValue().isOpen() && entry.getValue() != conn) {
                            entry.getValue().send(rawMessage);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("[WebSocket Server] Error: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("[WebSocket Server] Server started successfully on port " + getPort());
    }

    public boolean isUserOnline(String userId) {
        WebSocket ws = userConnMap.get(userId);
        return ws != null && ws.isOpen();
    }
}
