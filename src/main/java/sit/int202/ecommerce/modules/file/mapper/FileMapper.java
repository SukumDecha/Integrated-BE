package sit.int202.ecommerce.modules.file.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sit.int202.ecommerce.modules.file.model.FileEntity;
import sit.int202.ecommerce.modules.file.dto.FileResponse;

@Component
@RequiredArgsConstructor
public class FileMapper {

    public FileResponse toResponse(FileEntity file) {
        return new FileResponse(file);
    }


}