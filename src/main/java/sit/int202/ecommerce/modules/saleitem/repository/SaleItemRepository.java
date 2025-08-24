package sit.int202.ecommerce.modules.saleitem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import sit.int202.ecommerce.modules.saleitem.model.SaleItem;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer>, JpaSpecificationExecutor<SaleItem> {
    Page<SaleItem> findByBrand_NameIn(List<String> brandNames, Pageable pageable);
    List<SaleItem> findByBrand_NameIn(List<String> brandNames);

    @Query("select distinct s.storageGb from SaleItem s order by s.storageGb asc")
    List<Integer> findDistinctStorageGb();

    @Query("select distinct s.storageGb from SaleItem s order by s.storageGb asc")
    List<Integer> findDistinctStorageGbIncludingNull();




}