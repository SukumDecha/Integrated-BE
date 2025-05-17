package sit.int202.ecommerce.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.dto.request.BrandUpdateRequest;
import sit.int202.ecommerce.dto.response.BrandResponse;
import sit.int202.ecommerce.dto.response.BrandUpdateResponse;
import sit.int202.ecommerce.exception.BrandNotFoundException;
import sit.int202.ecommerce.exception.SaleItemNotFoundException;
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

    public Brand findById(Integer id) {
        return brandRepository.findById(id).orElseThrow(
                () -> new BrandNotFoundException("Brand not found for this id :: " + id)
        );
    }

    public Brand findByName(String name) {
        return brandRepository.findByName(name).orElseThrow(
                () -> new BrandNotFoundException("Brand not found for this name :: " + name)
        );
    }

    public BrandUpdateResponse updateBrand(Integer id, BrandUpdateRequest payload) {
        Brand existingBrand = findById(id);

        if (payload.getName() != null && !payload.getName().isEmpty()) {
            Brand brandWithSameName = brandRepository.findByName(payload.getName()).orElse(null);
            if (brandWithSameName != null && !brandWithSameName.getId().equals(existingBrand.getId())) {
                throw new BrandNotFoundException("Brand with name " + payload.getName() + " already exists.");
            }
        }

        if (payload.getName() != null) {
            existingBrand.setName(payload.getName());
        }
        if (payload.getWebsiteUrl() != null) {
            existingBrand.setWebsiteUrl(payload.getWebsiteUrl());
        }
        if (payload.getCountryOfOrigin() != null) {
            existingBrand.setCountryOfOrigin(payload.getCountryOfOrigin());
        }
        if (payload.getIsActive() != null) {
            existingBrand.setIsActive(payload.getIsActive());
        }

        return mapper.map(brandRepository.save(existingBrand), BrandUpdateResponse.class);
    }

    public void deleteBrandById(Integer id) {
        if (!brandRepository.existsById(id)) {
            throw new BrandNotFoundException("Brand with ID " + id + " not found");
        }
        brandRepository.deleteById(id);
    }
}
