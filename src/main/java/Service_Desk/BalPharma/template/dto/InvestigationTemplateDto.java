package Service_Desk.BalPharma.template.dto;

import Service_Desk.BalPharma.template.entity.InvestigationTemplateEntity;
import lombok.Data;

@Data
public class InvestigationTemplateDto {
    private Long id;
    private String title;
    private String icon;
    private String color;
    private String type;
    private String content;
    private Integer displayOrder;

    public static InvestigationTemplateDto from(InvestigationTemplateEntity e) {
        InvestigationTemplateDto dto = new InvestigationTemplateDto();
        dto.setId(e.getId());
        dto.setTitle(e.getTitle());
        dto.setIcon(e.getIcon());
        dto.setColor(e.getColor());
        dto.setType(e.getType());
        dto.setContent(e.getContent());
        dto.setDisplayOrder(e.getDisplayOrder());
        return dto;
    }
}