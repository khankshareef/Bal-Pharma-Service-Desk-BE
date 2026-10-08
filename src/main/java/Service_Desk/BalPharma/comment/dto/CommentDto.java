package Service_Desk.BalPharma.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long ticketId;
    private Long authorId;
    private String authorName;
    private String authorInitials;
    private String body;
    private String createdAt;

}