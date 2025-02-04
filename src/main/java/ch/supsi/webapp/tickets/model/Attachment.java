package ch.supsi.webapp.tickets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.*;
import org.apache.commons.io.FileUtils;

/**
 * La classe Attachment rappresenta un allegato associabile a un ticket.
 * È una classe incorporabile (annotata con @Embeddable) che può essere inclusa
 * in altre entità, come Ticket, per memorizzare file e relativi metadati.
 *
 * Annotazioni:
 * - `@Embeddable`: Indica che questa classe può essere incorporata in altre entità JPA.
 * - `@Lob`: Specifica che il campo `bytes` rappresenta un grande oggetto binario (BLOB).
 * - `@Column(columnDefinition="MEDIUMBLOB")`: Configura il campo come un MEDIUMBLOB nel database, adatto per file di dimensioni moderate.
 * - `@Getter`, `@Setter`: Generano automaticamente i metodi getter e setter.
 * - `@AllArgsConstructor`: Crea un costruttore con tutti i campi.
 * - `@NoArgsConstructor`: Crea un costruttore vuoto.
 * - `@Builder`: Fornisce un pattern builder per creare facilmente istanze di Attachment.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Attachment {

    /**
     * Contenuto binario del file (in formato byte array).
     * Questo campo è configurato come MEDIUMBLOB nel database.
     */
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] bytes;

    /**
     * Nome del file associato all'allegato.
     */
    private String name;

    /**
     * Tipo MIME del file, come "application/pdf" o "image/png".
     */
    private String contentType;

    /**
     * Dimensione del file in byte.
     */
    private Long size;

    /**
     * Restituisce una rappresentazione leggibile della dimensione del file.
     * Utilizza il metodo `FileUtils.byteCountToDisplaySize` per convertire i byte
     * in una stringa formattata (es. "10 KB", "2 MB").
     *
     * @return Una stringa che rappresenta la dimensione del file in formato leggibile.
     */
    public String getReadeableSize() {
        return FileUtils.byteCountToDisplaySize(size);
    }
}
