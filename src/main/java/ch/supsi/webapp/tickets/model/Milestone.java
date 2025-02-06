package ch.supsi.webapp.tickets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
@Entity
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Date date;
    private LocalDate dueDate;

    @ManyToOne
    private User author;


    @Enumerated(EnumType.STRING)
    private MilestoneStatus status;


    @OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> listOfTickets = new ArrayList<>();

    private float progress;

    public void addTicket(Ticket ticket){
        listOfTickets.add(ticket);
    }

    public void updateProgress() {
        if (listOfTickets == null || listOfTickets.isEmpty()) {
            this.progress = 0;
            return;
        }

        long doneCount = listOfTickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.DONE)
                .count();

        this.progress = (float) doneCount / listOfTickets.size() * 100;

        for (Ticket ticket : listOfTickets) {
            System.out.println(ticket.getTitle());
        }
    }

}
