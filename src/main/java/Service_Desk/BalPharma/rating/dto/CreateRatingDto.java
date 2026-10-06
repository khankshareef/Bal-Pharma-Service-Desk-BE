package Service_Desk.BalPharma.rating.dto;

import lombok.Data;

@Data
public class CreateRatingDto {
    private Long ticketId;
    private Integer rating;
    private String comments;
}