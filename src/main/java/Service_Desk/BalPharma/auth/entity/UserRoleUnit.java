package Service_Desk.BalPharma.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(
        name = "user_role_units",
        uniqueConstraints = @UniqueConstraint(
                name = "user_role_units_user_role_unit_unique",
                columnNames = {"user_id", "role", "unit_code"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AuthEntity user;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "unit_code", nullable = false, length = 120)
    private String unitCode;

    public UserRoleUnit(AuthEntity user, String role, String unitCode) {
        this.user = user;
        this.role = role;
        this.unitCode = unitCode;
    }
}