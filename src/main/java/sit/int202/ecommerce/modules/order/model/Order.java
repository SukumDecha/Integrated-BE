package sit.int202.ecommerce.modules.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import sit.int202.ecommerce.modules.user.model.UserAccount;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "buyerId", nullable = false)
    private UserAccount buyer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "sellerId", nullable = false)
    private UserAccount seller;

    @NotNull
    @Lob
    @Column(name = "shippingAddress", nullable = false)
    private String shippingAddress;

    @Lob
    @Column(name = "orderNote")
    private String orderNote;

    @NotNull
    @ColumnDefault("'COMPLETED'")
    @Lob
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "viewedBySeller", nullable = false)
    private Boolean viewedBySeller = false;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "orderDate")
    private Instant orderDate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdOn", updatable = false)
    private Instant createdOn;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updatedOn")
    private Instant updatedOn;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;


}