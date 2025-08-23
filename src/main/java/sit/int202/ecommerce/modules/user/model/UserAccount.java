package sit.int202.ecommerce.modules.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Data
@Entity
@Table(name = "user_account" ,
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "nickname")
        })

public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UserAccountType type;

    @Size(max = 50)
    @NotNull
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 100)
    @NotNull
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Size(max = 40)
    @NotNull
    @Column(name = "fullname", nullable = false, length = 40)
    private String fullname;

    @Size(max = 20)
    @Column(name = "mobileNumber", length = 20)
    private String mobileNumber;

    @Size(max = 30)
    @Column(name = "bankAccountNumber", length = 30)
    private String bankAccountNumber;

    @Size(max = 100)
    @Column(name = "bankName", length = 100)
    private String bankName;

    @Size(max = 20)
    @Column(name = "nationalId", length = 20)
    private String nationalId;

    @Size(max = 255)
    @Column(name = "nationalIdFrontImage")
    private String nationalIdFrontImage;

    @Size(max = 255)
    @Column(name = "nationalIdBackImage")
    private String nationalIdBackImage;

    @Column(nullable = false)
    private boolean isActive = false;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdOn")
    private Instant createdOn;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updatedOn")
    private Instant updatedOn;

}