package ch.supsi.webapp.tickets.Controller;

import ch.supsi.webapp.tickets.model.Tag;
import ch.supsi.webapp.tickets.model.Ticket;
import ch.supsi.webapp.tickets.repository.TagRepository;
import ch.supsi.webapp.tickets.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TagController {

    private final TicketService ticketService;
    private final TagRepository tagRepository;

    public TagController(TicketService ticketService, TagRepository tagRepository) {
        this.ticketService = ticketService;
        this.tagRepository = tagRepository;
    }

    @PostMapping("/{ticketId}/tags")
    public ResponseEntity<Map<String, Object>> addTag(@PathVariable int ticketId, @RequestBody Map<String, String> request) {
        String tagName = request.get("tag").trim().replace(" ", "").toLowerCase();

        Ticket ticket = ticketService.get(ticketId);
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Ticket not found"));
        }

        Tag tag = tagRepository.findByName(tagName).orElseGet(() -> {
            Tag newTag = new Tag(tagName);
            return tagRepository.save(newTag);
        });

        ticket.addTag(tag);
        ticketService.save(ticket);

        return ResponseEntity.ok(Map.of("success", true, "tag", tagName));
    }
}
