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

    /**
     * Mostra la pagina per aggiungere un commento a un ticket.
     *
     * @param ticketId         ID del ticket a cui aggiungere il commento.
     * @param parentCommentId  ID del commento padre, se il commento è una risposta (può essere nullo).
     * @param model            Modello per aggiungere gli attributi necessari alla vista.
     * @return Nome della vista "add-comment".
     */
    @GetMapping("/add")
    public String showAddCommentPage(@RequestParam int ticketId,
                                     @RequestParam(required = false) Long parentCommentId,
                                     Model model) {
        // Recupera il ticket specificato
        Ticket ticket = ticketService.get(ticketId);

        // Recupera il commento padre se l'ID è specificato e valido
        Comment parentComment = (parentCommentId != null && parentCommentId != 0) ? commentService.get(parentCommentId) : null;

        // Aggiunge il ticket e il commento padre al modello
        model.addAttribute("ticket", ticket);
        model.addAttribute("parentComment", parentComment);

        // Restituisce la vista "add-comment"
        return "add-comment";
    }

    /**
     * Gestisce l'aggiunta di un nuovo commento a un ticket.
     *
     * @param ticketId         ID del ticket a cui aggiungere il commento.
     * @param parentCommentId  ID del commento padre, se applicabile (può essere nullo).
     * @param text             Testo del commento.
     * @param principal        Informazioni sull'utente autenticato.
     * @return Redirect alla pagina di dettaglio del ticket.
     */
    @PostMapping("/add")
    public String addComment(@RequestParam int ticketId,
                             @RequestParam(required = false) Long parentCommentId,
                             @RequestParam String text,
                             Principal principal) {
        // Recupera il ticket specificato
        Ticket ticket = ticketService.get(ticketId);

        // Recupera il commento padre se specificato
        Comment parentComment = (parentCommentId != null) ? commentService.get(parentCommentId) : null;

        // Recupera l'utente autenticato
        User user = ticketService.findUserByUsername(principal.getName());

        // Crea un nuovo commento e imposta i suoi dettagli
        Comment comment = new Comment();
        comment.setText(text);
        comment.setCreatedDate(LocalDateTime.now());
        comment.setTicket(ticket);
        comment.setParent(parentComment);
        comment.setAuthor(user);

        // Salva il commento tramite il servizio
        commentService.saveComment(comment);

        // Redirect alla pagina di dettaglio del ticket
        return "redirect:/ticket/" + ticketId;
    }

    /**
     * Elimina un commento specificato dall'ID.
     *
     * @param id        ID del commento da eliminare.
     * @param principal Informazioni sull'utente autenticato.
     * @return ResponseEntity con lo stato della richiesta.
     *         - 200 OK: se il commento è stato eliminato con successo.
     *         - 403 Forbidden: se l'utente non è autorizzato a eliminare il commento.
     *         - 404 Not Found: se il commento non esiste.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Principal principal) {
        // Recupera il commento specificato dall'ID
        Comment comment = commentService.get(id);

        // Se il commento non esiste, restituisce 404 Not Found
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        // Controlla che l'utente autenticato sia l'autore del commento
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Elimina il commento tramite il servizio
        commentService.deleteComment(id);

        // Restituisce 200 OK per indicare che l'eliminazione è avvenuta con successo
        return ResponseEntity.ok().build();
    }
}
