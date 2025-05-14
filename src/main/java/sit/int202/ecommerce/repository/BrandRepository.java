package sit.int202.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int202.ecommerce.model.Brand;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    @Query("SELECT b FROM Brand b WHERE b.name = ?1")
    Optional<Brand> findByName(String name);

}