package Service_Desk.BalPharma.template.dto;

import Service_Desk.BalPharma.template.entity.TicketTemplateEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateResponseDto {
    private Long id;
    private String templateName;
    private Long departmentId;
    private String departmentName;
    private Long categoryId;
    private String categoryName;
    private Long subCategoryId;
    private String subCategoryName;
    private String priority;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TemplateResponseDto from(TicketTemplateEntity t) {
        return new TemplateResponseDto(
                t.getId(),
                t.getTemplateName(),
                t.getDepartment() != null ? t.getDepartment().getId() : null,
                t.getDepartment() != null ? t.getDepartment().getName() : null,
                t.getCategory() != null ? t.getCategory().getId() : null,
                t.getCategory() != null ? t.getCategory().getName() : null,
                t.getSubCategory() != null ? t.getSubCategory().getId() : null,
                t.getSubCategory() != null ? t.getSubCategory().getName() : null,
                t.getPriority(),
                t.getActive(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}