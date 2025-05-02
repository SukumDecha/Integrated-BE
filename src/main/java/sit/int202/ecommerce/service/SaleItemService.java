package sit.int202.ecommerce.service;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.dto.SaleItemGalleryResponse;
import sit.int202.ecommerce.exception.SaleItemNotFoundException;
import sit.int202.ecommerce.model.SaleItem;
import sit.int202.ecommerce.repository.SaleItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleItemService {
    private final SaleItemRepository repo;
    private final ModelMapper mapper;

    public SaleItemService(SaleItemRepository repo, ModelMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public List<SaleItemGalleryResponse> getAllSaleItems() {
        return repo.findAllByOrderByCreatedOnAsc().stream()
                .map(item -> mapper.map(item, SaleItemGalleryResponse.class))
                .collect(Collectors.toList());
    }

    public SaleItem getSaleItemById(Integer id) {
        SaleItem item = repo.findById(id).orElseThrow(
                () -> new SaleItemNotFoundException("SaleItem not found for this id :: " + id)
        );

        return item;
    }
}

