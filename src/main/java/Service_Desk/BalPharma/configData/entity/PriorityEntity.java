package Service_Desk.BalPharma.configData.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "config_priority")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriorityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "priority_code", nullable = false, unique = true, length = 30)
    private String priorityCode;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}