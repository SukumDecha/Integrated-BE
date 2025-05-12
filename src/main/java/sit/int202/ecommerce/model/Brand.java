package sit.int202.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "brand")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 30)
    @NotNull
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Size(max = 40)
    @Column(name = "websiteUrl", length = 40)
    private String websiteUrl;

    @NotNull
    @Column(name = "isActive", nullable = false)
    private Boolean isActive = false;

    @Size(max = 80)
    @Column(name = "countryOfOrigin", length = 80)
    private String countryOfOrigin;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdOn")
    private Instant createdOn;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updatedOn")
    private Instant updatedOn;

    @OneToMany(mappedBy = "brand")
    private Set<SaleItem> saleItems = new LinkedHashSet<>();

}