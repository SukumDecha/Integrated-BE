package sit.int202.ecommerce.modules.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.int202.ecommerce.modules.file.model.FileEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private String fileName;

    private String imageUrl;
    private Integer imageViewOrder;
    private String url;
    private String base64;

    public FileResponse(FileEntity file) {
        this.fileName = file.getStoredFilename();
        this.imageViewOrder = file.getDisplayOrder();
        this.imageUrl = "/uploads/" +
                file.getRefType() +
                "/" +
                file.getStoredFilename();
    }


}