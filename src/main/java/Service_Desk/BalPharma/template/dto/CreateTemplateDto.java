package Service_Desk.BalPharma.template.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTemplateDto {
    private String templateName;
    private Long departmentId;
    private Long categoryId;
    private Long subCategoryId;
    private String priority;
    private Boolean active;
}