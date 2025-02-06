package ch.supsi.webapp.tickets.repository;

import ch.supsi.webapp.tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findTop5ByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByDateDesc(String title, String description);
    List<Ticket> findByTags_Name(String tagName);
}
