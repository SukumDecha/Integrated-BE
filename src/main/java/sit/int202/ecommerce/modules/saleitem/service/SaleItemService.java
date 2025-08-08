package sit.int202.ecommerce.modules.saleitem.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.modules.brand.dto.response.BrandResponse;
import sit.int202.ecommerce.modules.brand.service.BrandService;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemCreateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemUpdateRequest;
import sit.int202.ecommerce.modules.brand.mapper.BrandMapper;
import sit.int202.ecommerce.modules.saleitem.mapper.SaleItemMapper;
import sit.int202.ecommerce.modules.brand.model.Brand;
import sit.int202.ecommerce.modules.saleitem.model.SaleItem;
import sit.int202.ecommerce.modules.saleitem.repository.SaleItemRepository;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemGalleryResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemListResponse;
import sit.int202.ecommerce.common.utils.PaginationUtil;
import sit.int202.ecommerce.common.dto.PaginateResponse;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SaleItemService {
    private final SaleItemRepository saleItemRepository;
    private final BrandService brandService;

    private final SaleItemMapper saleItemMapper;
    private final BrandMapper brandMapper;

    private final EntityManager em;

    public List<SaleItemGalleryResponse> getAllSaleItems() {
        return saleItemRepository.findAllByOrderByCreatedOnAscIdAsc().stream()
                .map(saleItemMapper::toDetailResponse)
                .collect(Collectors.toList());
    }

    public List<SaleItemListResponse> getAllSaleItemList() {
        return saleItemRepository.findAllByOrderByCreatedOnAscIdAsc().stream()
                .map(saleItemMapper::toListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SaleItemDetailResponse getSaleItemById(Integer id) {
        SaleItem item = saleItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("SaleItem not found for this id :: " + id)
        );

        return saleItemMapper.toDetailResponse(item);
    }


    @Transactional
    public SaleItemDetailResponse createSaleItem(SaleItemCreateRequest item) {
        item.normalize();

        BrandResponse brandDTO = brandService.getBrandById(item.getBrand().getId());
        Brand brand = brandMapper.toEntity(brandDTO);

        SaleItem tempSaleItem = saleItemMapper.toEntity(item);
        tempSaleItem.setBrand(brand);

        SaleItem saleItem = saleItemRepository.save(tempSaleItem);

        em.refresh(saleItem);
        return saleItemMapper.toDetailResponse(saleItem);
    }

    @Transactional
    public SaleItemDetailResponse updateSaleItem(Integer id, SaleItemUpdateRequest item) {
        item.normalize();

        SaleItem existingSaleItem = saleItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SaleItem not found with id :: " + id));

        // Don't touch the existing brand - important!
        BrandResponse brandDTO = brandService.getBrandById(item.getBrand().getId());
        Brand brand = brandMapper.toEntity(brandDTO);

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

        SaleItem updatedSaleItem = saleItemRepository.save(existingSaleItem);
        return saleItemMapper.toDetailResponse(updatedSaleItem);
    }

    public void deleteSaleItemById(Integer id) {
        if (!saleItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Sale item with ID " + id + " not found");
        }
        saleItemRepository.deleteById(id);
    }

    public PaginateResponse<SaleItemDetailResponse> getSaleItems(
            int page,
            int size,
            String sortField,
            String sortDirection,
            List<String> filterBrands
    ) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (sortField != null && !sortField.isBlank()) {
            Sort.Order order = new Sort.Order(Sort.Direction.fromString(sortDirection), sortField);

            sorts.add(order);
        }

        sorts.add(Sort.Order.asc("createdOn"));
        sorts.add(Sort.Order.asc("id"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sorts));

        Page<SaleItem> saleItems;
        if (filterBrands != null && !filterBrands.isEmpty()) {
            saleItems = saleItemRepository.findByBrand_NameIn(filterBrands, pageable);
        } else {
            saleItems = saleItemRepository.findAll(pageable);
        }

        Page<SaleItemDetailResponse> dtoPage = saleItems.map(saleItemMapper::toDetailResponse);

        return PaginationUtil.toPaginateResponse(dtoPage);
    }

}

