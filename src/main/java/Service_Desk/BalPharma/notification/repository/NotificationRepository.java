package Service_Desk.BalPharma.notification.repository;

import Service_Desk.BalPharma.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("""
        SELECT n FROM NotificationEntity n
        LEFT JOIN FETCH n.ticket t
        WHERE n.recipient.id = :userId
        ORDER BY n.createdAt DESC
    """)
    List<NotificationEntity> findByRecipientId(@Param("userId") Long userId);

    long countByRecipientIdAndIsReadFalse(Long userId);
}