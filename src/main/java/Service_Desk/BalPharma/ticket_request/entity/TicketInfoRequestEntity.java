package Service_Desk.BalPharma.ticket_request.entity;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_info_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketInfoRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private AuthEntity requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responded_by_id")
    private AuthEntity respondedBy;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(length = 4000)
    private String attachments;

    @Column(length = 4000)
    private String attachmentNames;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(length = 2000)
    private String response;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime respondedAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.status == null || this.status.isBlank()) {
            this.status = "PENDING";
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}