package sit.int202.ecommerce.modules.file.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "file_metadata")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "refType", nullable = false, length = 50)
    private String refType;

    @NotNull
    @Column(name = "refId", nullable = false)
    private Integer refId;

//    @Size(max = 50)
//    @NotNull
//    @Column(name = "usageType", nullable = false, length = 50)
//    private String usageType;

    @Size(max = 255)
    @NotNull
    @Column(name = "originalFilename", nullable = false)
    private String originalFilename;

    @Size(max = 255)
    @NotNull
    @Column(name = "storedFilename", nullable = false)
    private String storedFilename;

    @Size(max = 100)
    @NotNull
    @Column(name = "mimeType", nullable = false, length = 100)
    private String mimeType;

    @NotNull
    @Column(name = "fileSize", nullable = false)
    private Long fileSize;

    @Size(max = 500)
    @NotNull
    @Column(name = "filePath", nullable = false, length = 500)
    private String filePath;

    @ColumnDefault("0")
    @Column(name = "displayOrder")
    private Integer displayOrder;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdOn")
    private Instant createdOn;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updatedOn")
    private Instant updatedOn;

}