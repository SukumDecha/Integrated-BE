package sit.int202.ecommerce.modules.file.service;

import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.modules.file.model.FileEntity;

import java.util.List;

public interface FileService {

    /**
     * Get list of files by reference type and reference id
     * @param refType The type of reference (e.g., "SALE_ITEM", "USER_ACCOUNT", etc.)
     * @param refId The ID of the reference entity
     * @return List of FileEntity associated with the given reference type and ID
     */
    public List<FileEntity> getFilesByReference(String refType, Integer refId);

    /**
     * Upload multiple files and associate them with a reference type and ID
     * @param multipartFiles List of files to be uploaded
     * @param refType The type of reference (e.g., "SALE_ITEM", "USER_ACCOUNT", etc.)
     * @param refId The ID of the reference entity
     * @return List of FileEntity representing the uploaded files
     */
    public List<FileEntity> uploadMultipleFiles(List<MultipartFile> multipartFiles, String refType, Integer refId);

    /**
     * Save a single file and associate it with a reference type and ID
     * @param multipartFile The file to be uploaded
     * @param refType The type of reference (e.g., "SALE_ITEM", "USER_ACCOUNT", etc.)
     * @param refId The ID of the reference entity
     * @param order The display order of the file
     * @return FileEntity representing the uploaded file
     */
    public FileEntity uploadSingleFile(MultipartFile multipartFile, String refType, Integer refId, Integer order);

    /**
     * Delete a file by its ID
     * @param fileId The ID of the file to be deleted
     * @return true if the file was successfully deleted, false otherwise
     */
    public boolean deleteFileById(Integer fileId);


}
