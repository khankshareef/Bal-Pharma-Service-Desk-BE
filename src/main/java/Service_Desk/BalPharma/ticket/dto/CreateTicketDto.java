package Service_Desk.BalPharma.ticket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTicketDto {
    private String unitName;
    private String address;
    private Long departmentId;
    private Long categoryId;
    private Long subCategoryId;
    private Long templateId;
    private String attachmentUrls;
    private String attachmentNames;
    private String priority;
    private String subject;
    private String description;
    private String attachmentUrl;
    private String attachmentName;
}