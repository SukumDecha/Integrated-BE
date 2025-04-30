package sit.int202.ecommerce.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.model.SaleItem;
import sit.int202.ecommerce.repository.SaleItemRepository;

@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;
//
//    @Autowired
//    private ModelMapper modelMapper;

    public SaleItem getSaleItemById(Integer id) {
        SaleItem item = saleItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("SaleItem not found")
        );

       return item;
    }

}
