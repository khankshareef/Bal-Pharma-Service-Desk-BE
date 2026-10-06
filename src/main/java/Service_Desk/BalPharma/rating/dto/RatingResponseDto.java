package Service_Desk.BalPharma.rating.dto;

import Service_Desk.BalPharma.rating.entity.TicketRatingEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingResponseDto {
    private Long id;
    private Long ticketId;
    private String ticketCode;
    private Long ratedById;
    private String ratedByName;
    private Integer rating;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RatingResponseDto from(TicketRatingEntity e) {
        RatingResponseDto dto = new RatingResponseDto();
        dto.setId(e.getId());
        if (e.getTicket() != null) {
            dto.setTicketId(e.getTicket().getId());
            dto.setTicketCode(e.getTicket().getTicketCode());
        }
        if (e.getRatedBy() != null) {
            dto.setRatedById(e.getRatedBy().getId());
            dto.setRatedByName(e.getRatedBy().getName());
        }
        dto.setRating(e.getRating());
        dto.setComments(e.getComments());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}