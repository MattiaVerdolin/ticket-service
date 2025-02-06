package ch.supsi.webapp.tickets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.*;
import org.apache.commons.io.FileUtils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Attachment {

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] bytes;

    private String name;

    private String contentType;

    private Long size;

    public String getReadeableSize() {
        return FileUtils.byteCountToDisplaySize(size);
    }
}
