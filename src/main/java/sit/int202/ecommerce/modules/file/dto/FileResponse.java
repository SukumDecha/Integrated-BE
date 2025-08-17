package sit.int202.ecommerce.modules.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.int202.ecommerce.modules.file.model.File;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private Integer id;
    private String originalFilename;
    private String storedFilename;
    private String mimeType;
    private Long fileSize;
    private String filePath;       // path หรือ URL สำหรับใช้งานใน frontend
    private Integer displayOrder;
    private String refType;
    private Integer refId;
    private Instant createdOn;
    private Instant updatedOn;

    public FileResponse(File file) {
        this.id = file.getId();
        this.originalFilename = file.getOriginalFilename();
        this.storedFilename = file.getStoredFilename();
        this.mimeType = file.getMimeType();
        this.fileSize = file.getFileSize();
        this.filePath = file.getFilePath();
        this.displayOrder = file.getDisplayOrder();
        this.refType = file.getRefType();
        this.refId = file.getRefId();
        this.createdOn = file.getCreatedOn();
        this.updatedOn = file.getUpdatedOn();
    }
}