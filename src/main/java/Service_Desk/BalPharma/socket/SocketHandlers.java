package Service_Desk.BalPharma.socket;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketHandlers {

    private final SocketIOServer server;
    private final SocketAuthService authService;

    private final Map<Long, String> userSockets = new ConcurrentHashMap<>();

    @PostConstruct
    public void register() {

        server.addConnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            var userId = authService.extractUserId(token);

            if (userId.isEmpty()) {
                log.warn("Socket connection rejected — invalid token");
                client.disconnect();
                return;
            }

            Long uid = userId.get();
            userSockets.put(uid, client.getSessionId().toString());
            client.joinRoom("user:" + uid);

            log.info("Socket connected: user={} session={}", uid, client.getSessionId());
        });

        server.addDisconnectListener(client -> {
            userSockets.entrySet().removeIf(
                    e -> e.getValue().equals(client.getSessionId().toString())
            );
            log.info("Socket disconnected: session={}", client.getSessionId());
        });

        server.addEventListener("ticket:typing", Object.class, (client, data, ack) -> {
            if (!(data instanceof Map)) return;

            @SuppressWarnings("unchecked")
            Map<Object, Object> payload = (Map<Object, Object>) data;

            Object tid = payload.get("ticketId");
            Object uid = payload.get("userId");
            Object role = payload.get("activeRole");
            Object name = payload.get("name");
            Object typing = payload.get("isTyping");

            if (tid == null || uid == null) return;

            Map<String, Object> out = new HashMap<>();
            out.put("ticketId", Long.valueOf(tid.toString()));
            out.put("userId", Long.valueOf(uid.toString()));
            out.put("activeRole", role != null ? role.toString() : "");
            out.put("name", name != null ? name.toString() : "");
            out.put("isTyping", Boolean.TRUE.equals(typing));

            log.debug("ticket:typing relay → ticket={} user={} role={} typing={}",
                    tid, uid, role, typing);

            server.getBroadcastOperations().sendEvent("ticket:typing", out);
        });
    }

    public void emitToUser(Long userId, String event, Object payload) {
        if (userId == null) return;
        server.getRoomOperations("user:" + userId).sendEvent(event, payload);
    }

    public void broadcast(String event, Object payload) {
        server.getBroadcastOperations().sendEvent(event, payload);
    }
}