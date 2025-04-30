package sit.int202.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int202.ecommerce.model.Saleitem;

public interface SaleitemRepository extends JpaRepository<Saleitem, Integer> {
}