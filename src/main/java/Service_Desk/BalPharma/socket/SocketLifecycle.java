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
        server.start();
        log.info("Socket.IO server started on port {}", server.getConfiguration().getPort());
    }
}