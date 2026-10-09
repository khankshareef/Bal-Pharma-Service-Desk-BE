package Service_Desk.BalPharma.ticket.repository;

import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    @Query("""
        SELECT t FROM TicketEntity t
        LEFT JOIN FETCH t.createdBy
        LEFT JOIN FETCH t.department
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.subCategory
        LEFT JOIN FETCH t.template
        LEFT JOIN FETCH t.assignedTo
        WHERE t.id = :id
    """)
    Optional<TicketEntity> findByIdWithRelations(@Param("id") Long id);

    @Query("""
        SELECT t FROM TicketEntity t
        LEFT JOIN FETCH t.createdBy
        LEFT JOIN FETCH t.department
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.subCategory
        LEFT JOIN FETCH t.template
        LEFT JOIN FETCH t.assignedTo
        ORDER BY t.createdAt DESC
    """)
    List<TicketEntity> findAllWithRelations();

    @Query("""
        SELECT t FROM TicketEntity t
        LEFT JOIN FETCH t.createdBy
        LEFT JOIN FETCH t.department
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.subCategory
        LEFT JOIN FETCH t.template
        LEFT JOIN FETCH t.assignedTo
        WHERE t.createdBy.id = :userId
        ORDER BY t.createdAt DESC
    """)
    List<TicketEntity> findByCreatedById(@Param("userId") Long userId);

    long countByAssignedToIdAndStatusIn(Long executiveId, List<String> statuses);

    long countByAssignedToId(Long executiveId);

    long countByAssignedToIdAndStatus(Long executiveId, String status);

    long countByAssignedToIdAndSlaStatus(Long executiveId, String slaStatus);

    @Query("""
        SELECT t FROM TicketEntity t
        LEFT JOIN FETCH t.assignedTo
        LEFT JOIN FETCH t.department
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.subCategory
        LEFT JOIN FETCH t.createdBy
        WHERE t.assignedTo.id = :executiveId
        ORDER BY t.createdAt DESC
    """)
    List<TicketEntity> findByAssignedToId(@Param("executiveId") Long executiveId);

    @Query("""
        SELECT t FROM TicketEntity t
        LEFT JOIN FETCH t.assignedTo
        LEFT JOIN FETCH t.department
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.subCategory
        LEFT JOIN FETCH t.createdBy
        WHERE t.assignedTo.id = :executiveId
          AND t.status IN :statuses
        ORDER BY t.createdAt DESC
    """)
    List<TicketEntity> findByAssignedToIdAndStatusIn(
            @Param("executiveId") Long executiveId,
            @Param("statuses") List<String> statuses);

    @Query("""
        SELECT t FROM TicketEntity t
        LEFT JOIN FETCH t.assignedTo
        LEFT JOIN FETCH t.department
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.subCategory
        LEFT JOIN FETCH t.createdBy
        WHERE t.assignedTo IS NULL
          AND UPPER(t.status) = 'OPEN'
        ORDER BY t.createdAt ASC
    """)
    List<TicketEntity> findUnassignedOpenTickets();

    @Query("""
        SELECT t FROM TicketEntity t
        LEFT JOIN FETCH t.assignedTo
        LEFT JOIN FETCH t.department
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.subCategory
        LEFT JOIN FETCH t.createdBy
        WHERE t.assignedTo IS NULL
          AND UPPER(t.status) = 'OPEN'
          AND LOWER(t.department.name) = LOWER(:departmentName)
        ORDER BY t.createdAt ASC
    """)
    List<TicketEntity> findUnassignedOpenTicketsByDepartment(
            @Param("departmentName") String departmentName);

    @Query("""
        SELECT t FROM TicketEntity t
        WHERE t.status = 'CLOSED'
          AND t.closedAt IS NOT NULL
          AND t.closedAt < :cutoff
    """)
    List<TicketEntity> findStaleClosedTickets(@Param("cutoff") LocalDateTime cutoff);

    long countByCreatedById(Long userId);
    long countByStatus(String status);
    long countBySlaStatus(String slaStatus);
}