package ch.supsi.webapp.tickets.dto;

import ch.supsi.webapp.tickets.model.Ticket;
import ch.supsi.webapp.tickets.model.TicketStatus;
import ch.supsi.webapp.tickets.model.TicketType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * TicketDTO è un Data Transfer Object utilizzato per esporre i dati dei ticket
 * in un formato semplificato e leggibile per il client.
 * Questa classe rappresenta le informazioni principali di un ticket e può essere
 * utilizzata per operazioni di visualizzazione o trasferimento di dati nelle API REST.
 *
 * Annotazioni:
 * - `@Data`: Genera getter, setter, `toString`, `equals`, e `hashCode`.
 * - `@Builder`: Fornisce un pattern builder per creare facilmente istanze di TicketDTO.
 *
 * Campi:
 * - `id`: Identificativo unico del ticket.
 * - `title`: Titolo del ticket.
 * - `description`: Descrizione del ticket.
 * - `creationDate`: Data di creazione del ticket, formattata come "dd-MM-yyyy".
 * - `dueDate`: Data di scadenza del ticket, formattata come "dd-MM-yyyy".
 * - `timeEstimate`: Stima del tempo necessario per completare il ticket (in ore).
 * - `timeSpent`: Tempo effettivamente speso sul ticket (in ore).
 * - `author`: Nome utente dell'autore del ticket.
 * - `assignee`: Nome utente dell'assegnatario del ticket (può essere null).
 * - `status`: Stato del ticket (es. OPEN, IN_PROGRESS, DONE).
 * - `type`: Tipo di ticket (es. BUG, TASK, STORY).
 */
@Data
@Builder
public class TicketDTO {

    private int id;
    private String title;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dueDate;

    private int timeEstimate;
    private int timeSpent;
    private String author;
    private String assignee;
    private TicketStatus status;
    private TicketType type;

    /**
     * Metodo statico per convertire un oggetto Ticket in un TicketDTO.
     * Questo metodo mappa i dati dal modello Ticket al DTO, esponendo solo
     * le informazioni necessarie al client.
     *
     * @param ticket L'oggetto Ticket da convertire.
     * @return Un'istanza di TicketDTO contenente i dati mappati.
     */
    public static TicketDTO ticket2DTO(Ticket ticket) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .creationDate(ticket.getDate())
                .dueDate(ticket.getDueDate())
                .timeEstimate(ticket.getEstimate())
                .timeSpent(ticket.getTimeSpent())
                .author(ticket.getAuthor().getUsername())
                .assignee(ticket.getAssignee() != null ? ticket.getAssignee().getUsername() : null)
                .status(ticket.getStatus())
                .type(ticket.getType())
                .build();
    }
}
