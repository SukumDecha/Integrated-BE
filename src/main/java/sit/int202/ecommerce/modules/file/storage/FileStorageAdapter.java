package sit.int202.ecommerce.modules.file.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Interface for file storage operations.
 */
public interface FileStorageAdapter {

    /**
     * Store a file in the given subdirectory (or root if subDirectory is null).
     *
     * @param file           File to store
     * @param subDirectory   Optional subdirectory under root
     * @param storedFilename Name of the stored file
     * @return Full path of the stored file
     * @throws IOException if storing fails
     */
    String storeFile(MultipartFile file, String subDirectory, String storedFilename) throws IOException;

    /**
     * Delete a file from storage.
     *
     * @param storedFilename Name of the file to delete
     * @param subDirectory   Optional subdirectory
     * @return true if deleted successfully
     * @throws IOException if deletion fails
     */
    boolean deleteFile(String storedFilename, String subDirectory) throws IOException;

    /**
     * Get full path of a stored file.
     *
     * @param storedFilename Name of the file
     * @param subDirectory   Optional subdirectory
     * @return Full path of the file
     */
    String getFilePath(String storedFilename, String subDirectory);
}
