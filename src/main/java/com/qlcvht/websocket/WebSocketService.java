package com.qlcvht.websocket;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketService {

    private static WebSocketService instance;
    private static final int PORT = 8887;

    private ChatWebSocketServer server;
    private boolean serverStarted = false;
    private final Map<String, ChatWebSocketClient> clients = new ConcurrentHashMap<>();

    private WebSocketService() {}

    public static synchronized WebSocketService getInstance() {
        if (instance == null) {
            instance = new WebSocketService();
        }
        return instance;
    }

    public synchronized void startServer() {
        if (serverStarted && server != null) return;
        try {
            server = new ChatWebSocketServer(PORT);
            server.setReuseAddr(true);
            server.start();
            serverStarted = true;
            System.out.println("[WebSocketService] Embedded WebSocket Server started on port " + PORT);
        } catch (Exception e) {
            System.err.println("[WebSocketService] Server already running or port in use: " + e.getMessage());
            serverStarted = true; // Assume another instance is running the server
        }
    }

    public synchronized ChatWebSocketClient getClient(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "ANONYMOUS";
        }

        ChatWebSocketClient existing = clients.get(userId);
        if (existing != null && existing.isOpen()) {
            return existing;
        }

        startServer();

        try {
            // Small delay to ensure server started
            Thread.sleep(100);
            URI uri = new URI("ws://localhost:" + PORT);
            ChatWebSocketClient client = new ChatWebSocketClient(uri, userId);
            client.connect();
            clients.put(userId, client);
            return client;
        } catch (Exception e) {
            System.err.println("[WebSocketService] Error connecting client for " + userId + ": " + e.getMessage());
            return null;
        }
    }

    public ChatWebSocketServer getServer() {
        return server;
    }
}
