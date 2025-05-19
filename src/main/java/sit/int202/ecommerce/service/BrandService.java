package sit.int202.ecommerce.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.dto.request.BrandCreateRequest;
import sit.int202.ecommerce.dto.request.BrandUpdateRequest;
import sit.int202.ecommerce.dto.response.BrandDetailResponse;
import sit.int202.ecommerce.dto.response.BrandResponse;
import sit.int202.ecommerce.exception.BrandHasSaleItemsException;
import sit.int202.ecommerce.model.Brand;
import sit.int202.ecommerce.repository.BrandRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ModelMapper mapper;

    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(item -> mapper.map(item, BrandResponse.class))
                .collect(Collectors.toList());
    }

    public BrandDetailResponse getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Brand not found for this id :: " + id)
        );

        return mapper.map(brand, BrandDetailResponse.class);
    }

    public BrandDetailResponse createBrand(BrandCreateRequest request) {
        request.normalize();

        if (brandRepository.findByName(request.getName()).isPresent()) {
            throw new EntityExistsException("Brand with name " + request.getName() + " already exists.");
        }

        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setWebsiteUrl(request.getWebsiteUrl());
        brand.setCountryOfOrigin(request.getCountryOfOrigin());
        brand.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Brand savedBrand = brandRepository.save(brand);

        return mapper.map(savedBrand, BrandDetailResponse.class);

    }

    public BrandDetailResponse updateBrand(Integer id, BrandUpdateRequest payload) {
        payload.normalize();

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

        return mapper.map(brandRepository.save(existingBrand), BrandDetailResponse.class);
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
