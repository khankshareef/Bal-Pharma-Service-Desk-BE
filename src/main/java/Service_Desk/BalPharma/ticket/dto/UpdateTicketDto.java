package Service_Desk.BalPharma.ticket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTicketDto {
    private String unitName;
    private String address;
    private Long departmentId;
    private Long categoryId;
    private Long subCategoryId;
    private String priority;
    private String subject;
    private String description;
    private String attachmentUrls;
    private String attachmentNames;
    private String attachmentUrl;
    private String attachmentName;
}