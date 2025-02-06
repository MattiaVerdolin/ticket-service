package ch.supsi.webapp.tickets.Controller;

import ch.supsi.webapp.tickets.model.Comment;
import ch.supsi.webapp.tickets.model.Ticket;
import ch.supsi.webapp.tickets.model.User;
import ch.supsi.webapp.tickets.service.CommentService;
import ch.supsi.webapp.tickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final TicketService ticketService;

    @GetMapping("/add")
    public String showAddCommentPage(@RequestParam int ticketId,
                                     @RequestParam(required = false) Long parentCommentId,
                                     Model model) {

        Ticket ticket = ticketService.get(ticketId);

        Comment parentComment = (parentCommentId != null && parentCommentId != 0) ? commentService.get(parentCommentId) : null;

        model.addAttribute("ticket", ticket);
        model.addAttribute("parentComment", parentComment);

        return "add-comment";
    }

    @PostMapping("/add")
    public String addComment(@RequestParam int ticketId,
                             @RequestParam(required = false) Long parentCommentId,
                             @RequestParam String text,
                             Principal principal) {

        Ticket ticket = ticketService.get(ticketId);

        Comment parentComment = (parentCommentId != null) ? commentService.get(parentCommentId) : null;

        User user = ticketService.findUserByUsername(principal.getName());

        Comment comment = new Comment();
        comment.setText(text);
        comment.setCreatedDate(LocalDateTime.now());
        comment.setTicket(ticket);
        comment.setParent(parentComment);
        comment.setAuthor(user);

        commentService.saveComment(comment);

        return "redirect:/ticket/" + ticketId;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Principal principal) {

        Comment comment = commentService.get(id);

        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        commentService.deleteComment(id);

        return ResponseEntity.ok().build();
    }
}
