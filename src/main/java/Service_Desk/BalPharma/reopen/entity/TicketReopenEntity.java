package Service_Desk.BalPharma.reopen.entity;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_reopens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketReopenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private AuthEntity requestedBy;

    @Column(length = 2000, nullable = false)
    private String reason;

    @Column(nullable = false)
    private String status = "PENDING";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private AuthEntity reviewedBy;

    @Column(length = 2000)
    private String reviewComments;

    private LocalDateTime reviewedAt;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}