package Service_Desk.BalPharma.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketServerConfig {

    @Bean(destroyMethod = "stop")
    public SocketIOServer socketIOServer(
            @Value("${app.socket.port:9092}") int port,
            @Value("${app.socket.host:127.0.0.1}") String host,
            @Value("${app.cors.allowed-origins:http://localhost:5173}") String originsCsv) {

        com.corundumstudio.socketio.Configuration config =
                new com.corundumstudio.socketio.Configuration();

        config.setHostname(host);
        config.setPort(port);
        config.setOrigin(originsCsv);

        config.setWorkerThreads(4);
        config.setBossThreads(1);

        config.setAllowCustomRequests(true);
        config.setPingTimeout(60000);
        config.setPingInterval(25000);

        JacksonJsonSupport jsonSupport = new JacksonJsonSupport(new JavaTimeModule()) {
            @Override
            protected void init(ObjectMapper objectMapper) {
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }
        };
        config.setJsonSupport(jsonSupport);

        return new SocketIOServer(config);
    }
}