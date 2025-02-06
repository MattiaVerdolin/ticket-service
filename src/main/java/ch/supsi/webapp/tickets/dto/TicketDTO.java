package ch.supsi.webapp.tickets.dto;

import ch.supsi.webapp.tickets.model.Ticket;
import ch.supsi.webapp.tickets.model.TicketStatus;
import ch.supsi.webapp.tickets.model.TicketType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

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
