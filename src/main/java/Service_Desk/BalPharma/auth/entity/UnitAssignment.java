package Service_Desk.BalPharma.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "unit_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnitAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_code")
    private String unitCode;

    @Column(name = "unit_name")
    private String unitName;

    private String address;
    private String portCode;
    private Double latitude;
    private Double longitude;
    private Integer radiusMeters;
}