package sit.int202.ecommerce.modules.file.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class LocalStorageService implements FileStorageAdapter {

    @Value("${app.file.upload-dir}")
    private String rootDir;

    @Override
    public String storeFile(MultipartFile file, String subDirectory, String storedFilename) throws IOException {
        Path destination = buildPath(subDirectory, storedFilename);
        Files.createDirectories(destination.getParent());
        file.transferTo(destination.toFile());
        log.info("File stored at: {}", destination.toAbsolutePath());
        return destination.toString();
    }

    @Override
    public boolean deleteFile(String storedFilename, String subDirectory) throws IOException {
        Path path = buildPath(subDirectory, storedFilename);
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Failed to delete file {} in subdirectory {}", storedFilename, subDirectory, e);
            return false;
        }
    }

    @Override
    public String getFilePath(String storedFilename, String subDirectory) {
        return buildPath(subDirectory, storedFilename).toString();
    }

    /**
     * Build the full Path for a file, including optional subdirectory.
     */
    private Path buildPath(String subDirectory, String filename) {
        return subDirectory == null || subDirectory.isBlank()
                ? Paths.get(rootDir, filename)
                : Paths.get(rootDir, subDirectory, filename);
    }
}
