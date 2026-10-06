import Service_Desk.BalPharma.auth.repository.AuthRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final AuthRepository authRepository;

    public DebugController(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @GetMapping("/db")
    public Map<String, Object> db() {
        return Map.of(
                "total_users",   authRepository.count(),
                "sm001_found",   authRepository.findByEmployeeId("SM001").isPresent(),
                "sm001_id",      authRepository.findByEmployeeId("SM001")
                        .map(u -> u.getId()).orElse(null)
        );
    }
}