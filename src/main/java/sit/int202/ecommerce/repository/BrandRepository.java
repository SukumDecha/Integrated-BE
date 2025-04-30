package sit.int202.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int202.ecommerce.model.Brand;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}