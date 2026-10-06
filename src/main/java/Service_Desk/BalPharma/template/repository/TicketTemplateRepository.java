package Service_Desk.BalPharma.template.repository;

import Service_Desk.BalPharma.template.entity.TicketTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketTemplateRepository extends JpaRepository<TicketTemplateEntity, Long> {

    @Query("""
        SELECT t FROM TicketTemplateEntity t
        LEFT JOIN FETCH t.department
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.subCategory
    """)
    List<TicketTemplateEntity> findAllWithRelations();

    @Query("""
        SELECT t FROM TicketTemplateEntity t
        LEFT JOIN FETCH t.department
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.subCategory
        WHERE t.id = :id
    """)
    Optional<TicketTemplateEntity> findByIdWithRelations(@Param("id") Long id);
}