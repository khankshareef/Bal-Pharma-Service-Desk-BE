package Service_Desk.BalPharma.unit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "units",
        uniqueConstraints = { @UniqueConstraint(columnNames = "unit_code") }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_code", nullable = false, unique = true)
    private String unitCode;

    @Column(nullable = false)
    private String unitName;

    @Column(nullable = false)
    private String address;

    @Column
    private String portCode;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private Integer radiusMeters = 500;

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}