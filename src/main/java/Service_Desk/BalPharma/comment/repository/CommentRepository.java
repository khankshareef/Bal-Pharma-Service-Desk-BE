package Service_Desk.BalPharma.comment.repository;

import Service_Desk.BalPharma.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}