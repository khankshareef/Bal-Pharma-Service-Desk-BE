package Service_Desk.BalPharma.auth.mapper;

import Service_Desk.BalPharma.auth.dto.AuthDto;
import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.location.Role;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Component
public class AuthMapper {

    public AuthEntity toEntity(AuthDto dto) {
        AuthEntity entity = new AuthEntity();
        entity.setEmployeeId(dto.getEmployeeId());

        Set<Role> roles = resolveRoles(dto);
        entity.setRoles(roles);


        entity.setPassword(dto.getPassword());
        return entity;
    }


    public AuthDto toDto(AuthEntity entity) {
        AuthDto dto = new AuthDto();
        dto.setEmployeeId(entity.getEmployeeId());

        List<String> roleNames = entity.getRoles() == null
                ? List.of()
                : entity.getRoles().stream().map(Enum::name).toList();

        dto.setRoles(roleNames);

        Role fallback = pickHighestPrivilegeRole(entity.getRoles());
        dto.setPrimaryRole(fallback != null ? fallback.name() : null);
        dto.setRole(fallback != null ? fallback.name() : null);

        return dto;
    }

    private static final Role[] ROLE_HIERARCHY = {
            Role.SUPER_MANAGER,
            Role.ADMIN,
            Role.DEPUTY_MANAGER,
            Role.EXECUTIVE,
            Role.EMPLOYEE,
    };

    private Set<Role> resolveRoles(AuthDto dto) {
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            EnumSet<Role> set = EnumSet.noneOf(Role.class);
            for (String r : dto.getRoles()) {
                if (r == null || r.isBlank()) continue;
                set.add(Role.valueOf(r.trim().toUpperCase()));
            }
            return set;
        }

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            return EnumSet.of(Role.valueOf(dto.getRole().trim().toUpperCase()));
        }

        return EnumSet.noneOf(Role.class);
    }

    private Role pickHighestPrivilegeRole(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) return null;
        for (Role r : ROLE_HIERARCHY) {
            if (roles.contains(r)) return r;
        }
        return roles.iterator().next();
    }
}