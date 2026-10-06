package Service_Desk.BalPharma.ticket.entity;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.category.entity.CategoryEntity;
import Service_Desk.BalPharma.category.entity.SubCategoryEntity;
import Service_Desk.BalPharma.department.entity.DepartmentEntity;
import Service_Desk.BalPharma.template.entity.TicketTemplateEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketCode;

    @Column
    private String unitName;

    @Column
    private String address;

    @Column(length = 2000)
    private String resolutionNotes;

    @Column
    private String resolutionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private AuthEntity assignedTo;

    @Column
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private AuthEntity assignedBy;

    @Column(nullable = false)
    private Boolean autoAssigned = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private AuthEntity createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategoryEntity subCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private TicketTemplateEntity template;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(length = 4000)
    private String description;

    @Column(nullable = false)
    private String priority;

    @Column(nullable = false)
    private String status = "OPEN";

    @Column(nullable = false)
    private String slaStatus = "ON_TRACK";

    @Column(length = 4000)
    private String attachmentUrls;

    @Column(length = 4000)
    private String attachmentNames;

    private String attachmentUrl;
    private String attachmentName;
    private LocalDateTime closedAt;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime resolvedAt;


    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}