package ch.supsi.webapp.tickets.service;

import ch.supsi.webapp.tickets.model.Comment;
import ch.supsi.webapp.tickets.model.Ticket;
import ch.supsi.webapp.tickets.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByTicket(Ticket ticket) {
        return commentRepository.findByTicketOrderByCreatedDateDesc(ticket);
    }

    public void saveComment(Comment comment) {
        if (comment.getParent() != null) {
            Comment parent = commentRepository.findById(comment.getParent().getId()).orElseThrow(
                    () -> new IllegalArgumentException("Parent comment not found")
            );
            parent.getReplies().add(comment);
            commentRepository.save(parent); // Aggiorna il genitore con il nuovo figlio
        } else {
            commentRepository.save(comment);
        }
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public Comment get(Long id){
        return commentRepository.findById(id).orElse(null);
    }
}
