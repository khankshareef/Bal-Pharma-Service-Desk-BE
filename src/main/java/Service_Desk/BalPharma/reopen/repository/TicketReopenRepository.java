package Service_Desk.BalPharma.reopen.repository;

import Service_Desk.BalPharma.reopen.entity.TicketReopenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketReopenRepository extends JpaRepository<TicketReopenEntity, Long> {

    @Query("""
        SELECT r FROM TicketReopenEntity r
        LEFT JOIN FETCH r.ticket t
        LEFT JOIN FETCH t.createdBy
        LEFT JOIN FETCH t.department
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.subCategory
        LEFT JOIN FETCH r.requestedBy
        LEFT JOIN FETCH r.reviewedBy
        WHERE r.id = :id
    """)
    Optional<TicketReopenEntity> findByIdWithRelations(@Param("id") Long id);

    @Query("""
        SELECT r FROM TicketReopenEntity r
        LEFT JOIN FETCH r.ticket
        LEFT JOIN FETCH r.requestedBy
        LEFT JOIN FETCH r.reviewedBy
        ORDER BY r.createdAt DESC
    """)
    List<TicketReopenEntity> findAllWithRelations();

    @Query("""
        SELECT r FROM TicketReopenEntity r
        LEFT JOIN FETCH r.ticket
        LEFT JOIN FETCH r.requestedBy
        WHERE r.status = :status
        ORDER BY r.createdAt DESC
    """)
    List<TicketReopenEntity> findByStatus(@Param("status") String status);

    @Query("""
        SELECT r FROM TicketReopenEntity r
        LEFT JOIN FETCH r.ticket
        WHERE r.ticket.id = :ticketId
        ORDER BY r.createdAt DESC
    """)
    List<TicketReopenEntity> findByTicketId(@Param("ticketId") Long ticketId);

    boolean existsByTicketIdAndStatus(Long ticketId, String status);
}