package sit.int202.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "saleItem")
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "brandId", nullable = false)
    private Brand brand;

    @Size(max = 60)
    @NotNull
    @Column(name = "model", nullable = false, length = 60)
    private String model;

    @NotNull
    @Lob
    @Column(name = "description", nullable = false,  columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "ramGb")
    private Integer ramGb;

    @Column(name = "screenSizeInch", precision = 4, scale = 2)
    private BigDecimal screenSizeInch;

    @Column(name = "storageGb")
    private Integer storageGb;

    @Size(max = 100)
    @Column(name = "color", length = 100)
    private String color;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "createdOn",
            insertable = false,
            updatable = false
    )
//    @CreatedDate
    private Instant createdOn;


    @Column(name = "updatedOn",
            insertable = false,
            updatable = false
    )
//    @LastModifiedDate
    private Instant updatedOn;

}