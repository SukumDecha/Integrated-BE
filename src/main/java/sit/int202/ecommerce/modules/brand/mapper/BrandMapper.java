package sit.int202.ecommerce.modules.brand.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import sit.int202.ecommerce.modules.brand.dto.request.BrandCreateRequest;
import sit.int202.ecommerce.modules.brand.dto.response.BrandDetailResponse;
import sit.int202.ecommerce.modules.brand.dto.response.BrandResponse;
import sit.int202.ecommerce.modules.brand.model.Brand;

@Component
public class BrandMapper {

    private final ModelMapper modelMapper;

    public BrandMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public BrandResponse toResponse(Brand brand) {
        return modelMapper.map(brand, BrandResponse.class);
    }

    public BrandDetailResponse toDetailResponse(Brand brand) {
        return modelMapper.map(brand, BrandDetailResponse.class);
    }

    public Brand toEntity(BrandResponse response) {
        return modelMapper.map(response, Brand.class);
    }

    public Brand toEntity(BrandCreateRequest request) {
        return modelMapper.map(request, Brand.class);
    }

}
