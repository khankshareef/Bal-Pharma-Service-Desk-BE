package Service_Desk.BalPharma.auth.repository;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.location.AccountStatus;
import Service_Desk.BalPharma.location.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<AuthEntity, Long> {

    Optional<AuthEntity> findByEmployeeId(String employeeId);

    long countByStatus(AccountStatus status);

    long countByRolesContaining(Role role);

    @Modifying
    @Query("UPDATE AuthEntity u SET u.lastLoginAt = :at, u.lastLoginLocation = :loc WHERE u.id = :id")
    int updateLastLogin(@Param("id") Long id,
                        @Param("at") LocalDateTime at,
                        @Param("loc") String loc);
}