package Service_Desk.BalPharma.socket;

import Service_Desk.BalPharma.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocketAuthService {

    private final JwtService jwtService;

    public Optional<Long> extractUserId(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        try {
            SecretKey key = jwtService.getKey();
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Number id = claims.get("userId", Number.class);
            return id == null ? Optional.empty() : Optional.of(id.longValue());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}