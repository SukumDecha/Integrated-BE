package sit.int202.ecommerce.modules.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.int202.ecommerce.modules.file.model.File;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private String fileName;

    private String imageUrl;
    private Integer imageViewOrder;

    public FileResponse(File file) {
        this.fileName = file.getStoredFilename();
        this.imageViewOrder = file.getDisplayOrder();
        this.imageUrl = "/uploads/" +
                file.getRefType() +
                "/" +
                file.getStoredFilename();
    }
}