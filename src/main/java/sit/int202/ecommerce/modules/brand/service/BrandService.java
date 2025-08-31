package sit.int202.ecommerce.modules.brand.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.modules.brand.dto.request.BrandCreateRequest;
import sit.int202.ecommerce.modules.brand.dto.response.BrandDetailResponse;
import sit.int202.ecommerce.modules.brand.dto.response.BrandResponse;
import sit.int202.ecommerce.modules.brand.dto.request.BrandUpdateRequest;
import sit.int202.ecommerce.common.exceptions.BrandHasSaleItemsException;
import sit.int202.ecommerce.modules.brand.mapper.BrandMapper;
import sit.int202.ecommerce.modules.brand.model.Brand;
import sit.int202.ecommerce.modules.brand.repository.BrandRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandMapper brandMapper;

    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toResponse)
                .collect(Collectors.toList());
    }

    public BrandDetailResponse getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Brand not found for this id :: " + id)
        );

        return brandMapper.toDetailResponse(brand);
    }

    public BrandDetailResponse createBrand(BrandCreateRequest request) {
        if (brandRepository.findByName(request.getName()).isPresent()) {
            throw new EntityExistsException("Brand with name " + request.getName() + " already exists.");
        }

        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setWebsiteUrl(request.getWebsiteUrl());
        brand.setCountryOfOrigin(request.getCountryOfOrigin());
        brand.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Brand savedBrand = brandRepository.save(brand);

        return brandMapper.toDetailResponse(savedBrand);
    }

    public BrandDetailResponse updateBrand(Integer id, BrandUpdateRequest payload) {
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found for this id :: " + id));

        if (payload.getName() != null && !payload.getName().isEmpty()) {
            Brand brandWithSameName = brandRepository.findByName(payload.getName()).orElse(null);
            if (brandWithSameName != null && !brandWithSameName.getId().equals(existingBrand.getId())) {
                throw new EntityExistsException("Brand with name " + payload.getName() + " already exists.");
            }
        }

        existingBrand.setName(payload.getName());
        existingBrand.setWebsiteUrl(payload.getWebsiteUrl());
        existingBrand.setCountryOfOrigin(payload.getCountryOfOrigin());
        existingBrand.setIsActive(payload.getIsActive() != null ? payload.getIsActive() : existingBrand.getIsActive());

        return brandMapper.toDetailResponse(brandRepository.save(existingBrand));
    }

    public void deleteBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found for this id :: " + id));

        if (brand.getSaleItems() != null && !brand.getSaleItems().isEmpty()) {
            throw new BrandHasSaleItemsException("Cannot delete brand with existing sale items.");
        }

        brandRepository.deleteById(id);
    }
}
