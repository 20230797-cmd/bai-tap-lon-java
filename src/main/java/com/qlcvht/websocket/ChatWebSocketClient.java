package com.qlcvht.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.swing.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ChatWebSocketClient extends WebSocketClient {

    public interface MessageListener {
        void onMessageReceived(ChatMessage message);
        void onStatusChanged(boolean connected, String statusText);
    }

    private final String userId;
    private final List<MessageListener> listeners = new ArrayList<>();

    public ChatWebSocketClient(URI serverUri, String userId) {
        super(serverUri);
        this.userId = userId;
    }

    public synchronized void addListener(MessageListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[WebSocket Client] Connected to server as: " + userId);
        send("REGISTER:" + userId);
        notifyStatus(true, "🟢 WebSocket Online (Real-time)");
    }

    @Override
    public void onMessage(String rawMessage) {
        if (rawMessage.startsWith("REGISTER_OK:")) {
            System.out.println("[WebSocket Client] Register confirmed: " + rawMessage);
            return;
        }

        ChatMessage msg = ChatMessage.deserialize(rawMessage);
        if (msg != null) {
            SwingUtilities.invokeLater(() -> {
                for (MessageListener listener : listeners) {
                    listener.onMessageReceived(msg);
                }
            });
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[WebSocket Client] Closed connection: " + reason);
        notifyStatus(false, "🔴 Offline (Đang kết nối lại...)");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("[WebSocket Client] Error: " + ex.getMessage());
        notifyStatus(false, "⚠️ Lỗi kết nối WebSocket");
    }

    private void notifyStatus(boolean connected, String text) {
        SwingUtilities.invokeLater(() -> {
            for (MessageListener listener : listeners) {
                listener.onStatusChanged(connected, text);
            }
        });
    }

    public void sendChatMessage(ChatMessage msg) {
        if (isOpen() && msg != null) {
            send(msg.serialize());
        }
    }
}
