package Service_Desk.BalPharma.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatsDto {
    private long total;
    private long active;
    private long inactive;
    private long totalSubCategories;
}