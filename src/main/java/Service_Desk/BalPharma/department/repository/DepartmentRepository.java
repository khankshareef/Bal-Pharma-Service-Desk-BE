package Service_Desk.BalPharma.department.repository;

import Service_Desk.BalPharma.department.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {

    @Query("SELECT d FROM DepartmentEntity d LEFT JOIN FETCH d.unit")
    List<DepartmentEntity> findAllWithUnit();

    @Query("SELECT d FROM DepartmentEntity d LEFT JOIN FETCH d.unit WHERE d.id = :id")
    Optional<DepartmentEntity> findByIdWithUnit(@Param("id") Long id);

    @Query("""
    SELECT a.department, COUNT(a)
    FROM AuthEntity a
    WHERE a.department IS NOT NULL
    GROUP BY a.department
""")
    List<Object[]> countUsersByDepartment();

    Optional<DepartmentEntity> findByDepartmentCode(String departmentCode);
    boolean existsByNameIgnoreCase(String name);
    long countByActive(Boolean active);
}