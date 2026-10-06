package Service_Desk.BalPharma.category.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateCategoryDto {
    private String name;
    private String scope;
    private Boolean active;
    private Long departmentId;
    private List<String> subCategories;
}