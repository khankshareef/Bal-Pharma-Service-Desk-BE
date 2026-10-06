package Service_Desk.BalPharma.auth.entity;

import Service_Desk.BalPharma.location.AccountStatus;
import Service_Desk.BalPharma.location.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String employeeId;

    @Column(nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false)
    private Boolean firstTimeLogin = true;

    @Column(nullable = false)
    private String department;

    @Column
    private String primaryLocation;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Set<UnitAssignment> allowedLocations = new HashSet<>();

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<UserRoleUnit> roleUnitRows = new HashSet<>();

    private LocalDateTime lastLoginAt;

    @Column
    private String lastLoginLocation;

    @Enumerated(EnumType.STRING)
    private Service_Desk.BalPharma.location.Location currentLocationEnum;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime passwordLastReset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reset_by_id")
    private AuthEntity resetBy;

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public boolean hasRole(Role r) {
        return roles != null && roles.contains(r);
    }

    public boolean hasAnyRole(Role... rs) {
        if (roles == null || roles.isEmpty()) return false;
        for (Role r : rs) if (roles.contains(r)) return true;
        return false;
    }
}