package sit.int202.ecommerce.modules.file.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageAdapter {
    /**
     * Save file to the storage and return the stored filename.
     */
    String saveFile(MultipartFile file, String subDirectory, String storedFilename) throws IOException;

    /**
     * Delete file from the storage by stored filename.
     */
    boolean deleteFile(String storedFilename, String subDirectory) throws IOException;

    /**
     * Get full file path (or URL) from stored filename.
     */
    String getFilePath(String storedFilename, String subDirectory);

    /**
     * Store file with specific filename (used when filename is already generated).
     */
    String store(MultipartFile file, String storedFilename);  // ✅ เพิ่มตัวนี้

    /**
     * Delete file by stored filename (no sub-directory version).
     */
    boolean delete(String storedFilename); // ✅ เพิ่มตัวนี้

    /**
     * Get storage path (no sub-directory version).
     */
    String getStoragePath(String storedFilename); // ✅ เพิ่มตัวนี้
}