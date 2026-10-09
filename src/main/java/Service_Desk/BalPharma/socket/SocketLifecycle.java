package Service_Desk.BalPharma.socket;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketLifecycle {

    private final SocketIOServer server;

    @PostConstruct
    public void start() {
        try {
            server.start();
            log.info(
                    ">>> Socket.IO server STARTED on {}:{}",
                    server.getConfiguration().getHostname(),
                    server.getConfiguration().getPort()
            );
        } catch (Exception e) {
            log.error(">>> Socket.IO server FAILED to start", e);
            throw e;
        }
    }
}