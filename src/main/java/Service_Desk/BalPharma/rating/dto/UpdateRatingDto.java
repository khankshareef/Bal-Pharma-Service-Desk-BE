package Service_Desk.BalPharma.rating.dto;

import lombok.Data;

@Data
public class UpdateRatingDto {
    private Integer rating;
    private String comments;
}