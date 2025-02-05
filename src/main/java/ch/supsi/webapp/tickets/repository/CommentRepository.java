package ch.supsi.webapp.tickets.repository;

import ch.supsi.webapp.tickets.model.Comment;
import ch.supsi.webapp.tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Recupera tutti i commenti associati a un ticket, ordinati per data di creazione (dal più recente al più vecchio)
    List<Comment> findByTicketOrderByCreatedDateDesc(Ticket ticket);
}

