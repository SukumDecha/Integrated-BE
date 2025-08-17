package sit.int202.ecommerce.modules.file.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import sit.int202.ecommerce.modules.file.model.File;
import sit.int202.ecommerce.modules.file.dto.FileResponse;

@Component
@RequiredArgsConstructor
public class FileMapper {

    private final ModelMapper modelMapper;

    public FileResponse toResponse(File file) {
        return modelMapper.map(file, FileResponse.class);
    }
}