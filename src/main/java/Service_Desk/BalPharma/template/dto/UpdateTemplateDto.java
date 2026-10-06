package Service_Desk.BalPharma.template.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTemplateDto {
    private String templateName;
    private Long departmentId;
    private Long categoryId;
    private Long subCategoryId;
    private String priority;
    private Boolean active;
}