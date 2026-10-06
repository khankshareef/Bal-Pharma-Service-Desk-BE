package Service_Desk.BalPharma.ticket.dto;

import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDto {

    private Long id;
    private String ticketCode;

    private String unitName;
    private String address;

    private Long createdById;
    private String createdByName;
    private String createdByEmployeeId;

    private Long departmentId;
    private String departmentName;

    private Long categoryId;
    private String categoryName;

    private Long subCategoryId;
    private String subCategoryName;

    private Long templateId;
    private String templateName;

    private String subject;
    private String description;
    private String priority;
    private String status;
    private String slaStatus;

    private String attachmentUrl;
    private String attachmentName;
    private String attachmentUrls;
    private String attachmentNames;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    private String resolutionNotes;
    private String resolutionType;

    private Long reopenId;
    private String reopenStatus;
    private String reopenReason;
    private String reopenRequestedByName;
    private String reopenRequestedAt;
    private String reopenReviewComments;
    private String reopenReviewedByName;

    private Long assignedToId;
    private String assignedToName;
    private String assignedToEmployeeId;
    private LocalDateTime assignedAt;
    private Boolean autoAssigned;
    private LocalDateTime closedAt;

    public static TicketResponseDto from(TicketEntity t) {
        TicketResponseDto dto = new TicketResponseDto();

        dto.id = t.getId();
        dto.ticketCode = t.getTicketCode();
        dto.unitName = t.getUnitName();
        dto.address = t.getAddress();
        dto.closedAt = t.getClosedAt();

        if (t.getCreatedBy() != null) {
            dto.createdById = t.getCreatedBy().getId();
            dto.createdByName = t.getCreatedBy().getName();
            dto.createdByEmployeeId = t.getCreatedBy().getEmployeeId();
        }
        if (t.getDepartment() != null) {
            dto.departmentId = t.getDepartment().getId();
            dto.departmentName = t.getDepartment().getName();
        }
        if (t.getCategory() != null) {
            dto.categoryId = t.getCategory().getId();
            dto.categoryName = t.getCategory().getName();
        }
        if (t.getSubCategory() != null) {
            dto.subCategoryId = t.getSubCategory().getId();
            dto.subCategoryName = t.getSubCategory().getName();
        }
        if (t.getTemplate() != null) {
            dto.templateId = t.getTemplate().getId();
            dto.templateName = t.getTemplate().getTemplateName();
        }

        dto.subject = t.getSubject();
        dto.description = t.getDescription();
        dto.priority = t.getPriority();
        dto.status = t.getStatus();
        dto.slaStatus = t.getSlaStatus();

        dto.attachmentUrl  = t.getAttachmentUrl();
        dto.attachmentName = t.getAttachmentName();
        dto.attachmentUrls  = t.getAttachmentUrls();
        dto.attachmentNames = t.getAttachmentNames();

        dto.createdAt = t.getCreatedAt();
        dto.updatedAt = t.getUpdatedAt();
        dto.resolvedAt = t.getResolvedAt();

        dto.resolutionNotes = t.getResolutionNotes();
        dto.resolutionType = t.getResolutionType();

        if (t.getAssignedTo() != null) {
            dto.assignedToId = t.getAssignedTo().getId();
            dto.assignedToName = t.getAssignedTo().getName();
            dto.assignedToEmployeeId = t.getAssignedTo().getEmployeeId();
        }
        dto.assignedAt = t.getAssignedAt();
        dto.autoAssigned = t.getAutoAssigned();

        return dto;
    }
}