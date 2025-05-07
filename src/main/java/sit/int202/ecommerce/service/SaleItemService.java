package sit.int202.ecommerce.service;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.dto.request.SaleItemCreateRequest;
import sit.int202.ecommerce.dto.request.SaleItemUpdateRequest;
import sit.int202.ecommerce.dto.response.SaleItemGalleryResponse;
import sit.int202.ecommerce.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.exception.SaleItemNotFoundException;
import sit.int202.ecommerce.model.Brand;
import sit.int202.ecommerce.model.SaleItem;
import sit.int202.ecommerce.repository.SaleItemRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SaleItemService {
    private final SaleItemRepository saleItemRepository;
    private final BrandService brandService;
    private final ModelMapper mapper;

    public List<SaleItemGalleryResponse> getAllSaleItems() {
        return saleItemRepository.findAllByOrderByCreatedOnAscIdAsc().stream()
                .map(item -> mapper.map(item, SaleItemGalleryResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public SaleItemDetailResponse getSaleItemById(Integer id) {
        SaleItem item = saleItemRepository.findById(id).orElseThrow(
                () -> new SaleItemNotFoundException("SaleItem not found for this id :: " + id)
        );

        return mapper.map(item, SaleItemDetailResponse.class);
    }


    public SaleItemDetailResponse createSaleItem(SaleItemCreateRequest item) {
        item.normalize();

        Brand brand = brandService.findById(item.getBrand().getId());


        SaleItem reqSaleItem = mapper.map(item, SaleItem.class);
        reqSaleItem.setBrand(brand);

        SaleItem newSaleItem = saleItemRepository.save(reqSaleItem);
        return mapper.map(newSaleItem, SaleItemDetailResponse.class);
    }

    public SaleItemDetailResponse updateSaleItem(Integer id, SaleItemUpdateRequest item) {
        item.normalize();

        SaleItem existingSaleItem = saleItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SaleItem not found with id: " + id));

        // Don't touch the existing brand - important!
        Brand brand = brandService.findById(item.getBrand().getId());

        // Update fields
        existingSaleItem.setModel(item.getModel());
        existingSaleItem.setPrice(item.getPrice());
        existingSaleItem.setDescription(item.getDescription());
        existingSaleItem.setBrand(brand);
        existingSaleItem.setRamGb(item.getRamGb());
        existingSaleItem.setScreenSizeInch(item.getScreenSizeInch());
        existingSaleItem.setStorageGb(item.getStorageGb());
        existingSaleItem.setColor(item.getColor());
        existingSaleItem.setQuantity(item.getQuantity());

        return mapper.map(saleItemRepository.save(existingSaleItem), SaleItemDetailResponse.class);
    }

}

