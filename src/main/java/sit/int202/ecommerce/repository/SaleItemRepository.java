package sit.int202.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int202.ecommerce.model.SaleItem;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
    List<SaleItem> findAllByOrderByCreatedOnAsc();
}