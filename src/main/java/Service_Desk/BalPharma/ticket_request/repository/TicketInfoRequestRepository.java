package Service_Desk.BalPharma.ticket_request.repository;

import Service_Desk.BalPharma.ticket_request.entity.TicketInfoRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketInfoRequestRepository
        extends JpaRepository<TicketInfoRequestEntity, Long> {

    @Query("""
        SELECT r FROM TicketInfoRequestEntity r
        LEFT JOIN FETCH r.ticket
        LEFT JOIN FETCH r.requestedBy
        LEFT JOIN FETCH r.respondedBy
        WHERE r.ticket.id = :ticketId
        ORDER BY r.createdAt DESC
    """)
    List<TicketInfoRequestEntity> findByTicketId(@Param("ticketId") Long ticketId);

    @Query("""
        SELECT r FROM TicketInfoRequestEntity r
        LEFT JOIN FETCH r.ticket
        LEFT JOIN FETCH r.requestedBy
        LEFT JOIN FETCH r.respondedBy
        WHERE r.requestedBy.id = :userId
        ORDER BY r.createdAt DESC
    """)
    List<TicketInfoRequestEntity> findByRequestedById(@Param("userId") Long userId);

    @Query("""
        SELECT r FROM TicketInfoRequestEntity r
        LEFT JOIN FETCH r.ticket
        LEFT JOIN FETCH r.requestedBy
        LEFT JOIN FETCH r.respondedBy
        ORDER BY r.createdAt DESC
    """)
    List<TicketInfoRequestEntity> findAllWithRelations();

    @Query("""
        SELECT r FROM TicketInfoRequestEntity r
        LEFT JOIN FETCH r.ticket
        LEFT JOIN FETCH r.requestedBy
        LEFT JOIN FETCH r.respondedBy
        WHERE r.id = :id
    """)
    Optional<TicketInfoRequestEntity> findByIdWithRelations(@Param("id") Long id);

    long countByTicketIdAndStatus(Long ticketId, String status);
}