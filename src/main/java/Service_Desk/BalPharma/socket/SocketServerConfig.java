package Service_Desk.BalPharma.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SocketServerConfig {

    private SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer(
            @Value("${app.socket.port:9092}") int port,
            @Value("${app.socket.host:0.0.0.0}") String host,
            @Value("${app.cors.allowed-origins:http://localhost:5173}") String originsCsv) {

        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setOrigin(originsCsv);

        config.setWorkerThreads(4);
        config.setBossThreads(1);

        config.setAllowCustomRequests(true);
        config.setPingTimeout(60000);
        config.setPingInterval(25000);

        // Register JavaTimeModule so LocalDateTime serializes correctly.
        // In netty-socketio 2.0.12, modules go through the constructor —
        // there is no addModule(...) method.
        JacksonJsonSupport jsonSupport = new JacksonJsonSupport(
                new JavaTimeModule()
        ) {
            @Override
            protected void init(ObjectMapper objectMapper) {
                objectMapper.disable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                );
            }
        };
        config.setJsonSupport(jsonSupport);

        server = new SocketIOServer(config);
        return server;
    }

    @PreDestroy
    public void stop() {
        if (server != null) server.stop();
    }
}