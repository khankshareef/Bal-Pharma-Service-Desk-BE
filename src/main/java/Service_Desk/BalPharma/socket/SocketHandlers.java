package Service_Desk.BalPharma.socket;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketHandlers {

    private final SocketIOServer server;
    private final SocketAuthService authService;

    /** userId -> socket session id */
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
            userSockets.values()
                    .removeIf(sid -> sid.equals(client.getSessionId().toString()));
            log.info("Socket disconnected: session={}", client.getSessionId());
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