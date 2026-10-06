package Service_Desk.BalPharma.rating.entity;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_by_id", nullable = false)
    private AuthEntity ratedBy;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comments;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}