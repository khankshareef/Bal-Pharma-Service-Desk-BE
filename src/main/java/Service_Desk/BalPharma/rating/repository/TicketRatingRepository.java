package Service_Desk.BalPharma.rating.repository;

import Service_Desk.BalPharma.rating.entity.TicketRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRatingRepository extends JpaRepository<TicketRatingEntity, Long> {

    @Query("""
        SELECT r FROM TicketRatingEntity r
        LEFT JOIN FETCH r.ticket
        LEFT JOIN FETCH r.ratedBy
        WHERE r.ticket.id = :ticketId
        ORDER BY r.createdAt DESC
    """)
    List<TicketRatingEntity> findByTicketId(@Param("ticketId") Long ticketId);

    @Query("""
        SELECT r FROM TicketRatingEntity r
        LEFT JOIN FETCH r.ticket
        LEFT JOIN FETCH r.ratedBy
        WHERE r.ratedBy.id = :userId
        ORDER BY r.createdAt DESC
    """)
    List<TicketRatingEntity> findByRatedById(@Param("userId") Long userId);

    @Query("""
        SELECT r FROM TicketRatingEntity r
        LEFT JOIN FETCH r.ticket
        LEFT JOIN FETCH r.ratedBy
        ORDER BY r.createdAt DESC
    """)
    List<TicketRatingEntity> findAllWithRelations();

    @Query("""
        SELECT r FROM TicketRatingEntity r
        LEFT JOIN FETCH r.ticket
        LEFT JOIN FETCH r.ratedBy
        WHERE r.id = :id
    """)
    Optional<TicketRatingEntity> findByIdWithRelations(@Param("id") Long id);

    boolean existsByTicketIdAndRatedById(Long ticketId, Long ratedById);

    @Query("SELECT AVG(r.rating) FROM TicketRatingEntity r WHERE r.ticket.id = :ticketId")
    Double averageRatingForTicket(@Param("ticketId") Long ticketId);
}