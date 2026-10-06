package Service_Desk.BalPharma.auth.repository;

import Service_Desk.BalPharma.auth.entity.UserRoleUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleUnitRepository
        extends JpaRepository<UserRoleUnit, Long> {

    List<UserRoleUnit> findByUserId(Long userId);

    List<UserRoleUnit> findByUserIdAndRole(Long userId, String role);

    @Modifying
    @Query("DELETE FROM UserRoleUnit r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM UserRoleUnit r WHERE r.user.id = :userId AND r.role = :role")
    void deleteByUserIdAndRole(@Param("userId") Long userId,
                               @Param("role") String role);
}