package Service_Desk.BalPharma.category.repository;

import Service_Desk.BalPharma.category.entity.SubCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCategoryRepository extends JpaRepository<SubCategoryEntity, Long> {
}