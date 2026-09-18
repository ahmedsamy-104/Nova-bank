package com.Novabank.Customer.entity;

import com.Novabank.Customer.entity.Role;
import com.Novabank.Customer.entity.customerstatus;
import com.Novabank.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customers_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_customers_mobile_number", columnNames = "mobile_number")
        })
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "passwordHash")
public class customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "firstname", nullable = false, length = 50)
    private String Firstname;

    @NotBlank
    @Column(name = "lastname", nullable = false, length = 50)
    private String Lastname;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, length = 100) // ← شيل unique=true
    private String email;

    @NotBlank
    @Pattern(regexp = "^01[0125][0-9]{8}$", message = "Invalid Egyptian mobile number")
    @Column(name = "mobile_number", nullable = false, length = 11) // ← شيل unique=true
    private String mobileNumber;

    @Column(name = "password_hash", nullable = false)
    private byte[] passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private customerstatus status = customerstatus.PENDING_VERIFICATION;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Role",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "Roleid")
    )
    private Set<Role> roles = new HashSet<>();
}