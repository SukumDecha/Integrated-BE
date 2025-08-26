package sit.int202.ecommerce.modules.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.Where;
import sit.int202.ecommerce.modules.file.model.FileEntity;

import java.time.Instant;

@Getter
@Setter
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
    @Column(name = "idCardNumber", length = 20)
    private String idCardNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinFormula("(SELECT fm.id FROM file_metadata fm WHERE fm.refType = 'USER_ACCOUNT' AND fm.refId = id AND fm.usageType = 'idCardImageFront' LIMIT 1)")
    private FileEntity idCardImageFront;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinFormula("(SELECT fm.id FROM file_metadata fm WHERE fm.refType = 'USER_ACCOUNT' AND fm.refId = id AND fm.usageType = 'idCardImageBack' LIMIT 1)")
    private FileEntity idCardImageBack;

    @Column(nullable = false)
    private boolean isActive = false;

    @Column(name = "createdOn",
            insertable = false,
            updatable = false
    )
    private Instant createdOn;

    @Column(name = "updatedOn",
            insertable = false,
            updatable = false
    )
    private Instant updatedOn;
}