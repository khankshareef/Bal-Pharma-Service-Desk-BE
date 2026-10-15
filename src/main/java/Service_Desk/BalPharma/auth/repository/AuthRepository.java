package Service_Desk.BalPharma.auth.repository;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.location.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthEntity, Long> {

    Optional<AuthEntity> findByEmployeeId(String employeeId);

    @Query("UPDATE AuthEntity u SET u.lastLoginAt = :at, u.lastLoginLocation = :loc WHERE u.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    void updateLastLogin(
            @Param("id") Long id,
            @Param("at") java.time.LocalDateTime at,
            @Param("loc") String loc
    );

    long countByStatus(AccountStatus status);

    @Query("""
        SELECT DISTINCT u FROM AuthEntity u
        JOIN u.allowedLocations a
        WHERE a.unitCode = :unitCode
    """)
    List<AuthEntity> findAllByAllowedUnitCode(@Param("unitCode") String unitCode);

    @Query("""
        SELECT COUNT(DISTINCT u) FROM AuthEntity u
        JOIN u.allowedLocations a
        WHERE a.unitCode = :unitCode
    """)
    long countUsersByAllowedUnitCode(@Param("unitCode") String unitCode);

    @Query("""
        SELECT COUNT(DISTINCT u) FROM AuthEntity u
        JOIN u.allowedLocations a
        JOIN u.roles r
        WHERE a.unitCode = :unitCode
          AND r = :role
    """)
    long countUsersByUnitAndRole(
            @Param("unitCode") String unitCode,
            @Param("role") Service_Desk.BalPharma.location.Role role
    );
}