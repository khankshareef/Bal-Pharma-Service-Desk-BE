package Service_Desk.BalPharma.template.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "investigation_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestigationTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 40)
    private String icon;              // tool | globe | monitor | key | search | check | message | help

    @Column(length = 40)
    private String color;             // gray | blue | cyan | yellow | purple | green | red

    @Column(nullable = false, length = 20)
    private String type;              // INVESTIGATION | RESPONSE

    @Column(length = 4000)
    private String content;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Integer displayOrder = 0;

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