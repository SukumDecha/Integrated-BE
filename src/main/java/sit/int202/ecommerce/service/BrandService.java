package sit.int202.ecommerce.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.dto.response.BrandResponse;
import sit.int202.ecommerce.exception.BrandNotFoundException;
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
}
