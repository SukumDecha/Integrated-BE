package sit.int202.ecommerce.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.dto.SaleItemResponse;
import sit.int202.ecommerce.model.SaleItem;
import sit.int202.ecommerce.repository.SaleItemRepository;
import org.modelmapper.ModelMapper;
import sit.int202.ecommerce.dto.SaleItemGalleryResponse;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<SaleItemGalleryResponse> getAllSaleItems() {
        return saleItemRepository.findAllByOrderByCreatedOnAsc().stream()
                .map(item -> modelMapper.map(item, SaleItemGalleryResponse.class))
                .collect(Collectors.toList());
    }

    public SaleItemResponse getSaleItemById(Integer id) {
        SaleItem item = saleItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("SaleItem not found")
        );

       return modelMapper.map(item, SaleItemResponse.class);
    }

}
