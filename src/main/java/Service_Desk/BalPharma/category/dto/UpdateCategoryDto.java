package Service_Desk.BalPharma.category.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCategoryDto {
    private String name;
    private String scope;
    private Boolean active;
    private List<String> subCategories;    // replaces existing set if provided
}