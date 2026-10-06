package Service_Desk.BalPharma.category.repository;

import Service_Desk.BalPharma.category.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    boolean existsByNameIgnoreCase(String name);
    long countByActive(Boolean active);

    @Query("""
        SELECT DISTINCT c FROM CategoryEntity c
        LEFT JOIN FETCH c.department
        LEFT JOIN FETCH c.subCategories
        WHERE c.department.id = :departmentId
        AND (c.active = true OR c.active IS NULL)
        ORDER BY c.name
    """)
    List<CategoryEntity> findByDepartmentIdWithSubCategories(@Param("departmentId") Long departmentId);

    @Query("""
        SELECT DISTINCT c FROM CategoryEntity c
        LEFT JOIN FETCH c.department
        LEFT JOIN FETCH c.subCategories
        ORDER BY c.name
    """)
    List<CategoryEntity> findAllWithRelations();

    @Query("""
        SELECT c FROM CategoryEntity c
        LEFT JOIN FETCH c.department
        LEFT JOIN FETCH c.subCategories
        WHERE c.id = :id
    """)
    Optional<CategoryEntity> findByIdWithRelations(@Param("id") Long id);

    @Query("""
        SELECT c.department.id, COUNT(c)
        FROM CategoryEntity c
        WHERE c.department IS NOT NULL
        GROUP BY c.department.id
    """)
    List<Object[]> countCategoriesByDepartment();
}