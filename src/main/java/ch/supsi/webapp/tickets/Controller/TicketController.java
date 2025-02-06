package ch.supsi.webapp.tickets.Controller;

import ch.supsi.webapp.tickets.dto.Success;
import ch.supsi.webapp.tickets.dto.TicketDTO;
import ch.supsi.webapp.tickets.model.Milestone;
import ch.supsi.webapp.tickets.model.Ticket;
import ch.supsi.webapp.tickets.model.TicketStatus;
import ch.supsi.webapp.tickets.model.User;
import ch.supsi.webapp.tickets.service.MilestoneService;
import ch.supsi.webapp.tickets.service.TicketService;
import ch.supsi.webapp.tickets.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(TicketService service, UserService userService) {
        this.ticketService = service;
        this.userService = userService;
    }

    @GetMapping("")
    public List<TicketDTO> list() {
        return ticketService.getAll().stream()
                .map(TicketDTO::ticket2DTO)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TicketDTO> get(@PathVariable int id) {
        if (!ticketService.exists(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Ticket ticket = ticketService.get(id);
        return ticket != null
                ? new ResponseEntity<>(TicketDTO.ticket2DTO(ticket), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "")
    public ResponseEntity<TicketDTO> post(@RequestBody Ticket ticket) {
        ticket = ticketService.post(ticket);
        return new ResponseEntity<>(TicketDTO.ticket2DTO(ticket), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<TicketDTO> put(@PathVariable int id, @RequestBody Ticket ticket) {
        if (!ticketService.exists(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        ticket.setId(id);
        ticketService.put(ticket);
        return new ResponseEntity<>(TicketDTO.ticket2DTO(ticket), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Success> delete(@PathVariable int id) {
        if (!ticketService.exists(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        boolean remove = ticketService.delete(id);
        return new ResponseEntity<>(new Success(remove), HttpStatus.OK);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<TicketDTO> getTicketDetails(@PathVariable Integer id) {
        Ticket ticket = ticketService.get(id);
        TicketDTO ticketDTO = TicketDTO.ticket2DTO(ticket);
        return ResponseEntity.ok(ticketDTO);
    }

    @PostMapping("/watch/{id}")
    public ResponseEntity<String> watchTicket(@PathVariable Integer id, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        Ticket ticket = ticketService.get(id);

        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");
        }

        if (!user.getWatches().contains(ticket)) {
            user.getWatches().add(ticket);
            userService.save(user);
        }

        return ResponseEntity.ok("Ticket added to watches");
    }

    @PostMapping("/{id}/change-status")
    public ResponseEntity<Map<String, Object>> changeStatus(@PathVariable int id, @RequestBody Map<String, String> request) {
        Ticket ticket = ticketService.get(id);
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Ticket not found"));
        }

        try {
            TicketStatus newStatus = TicketStatus.valueOf(request.get("status"));
            ticket.setStatus(newStatus);
            ticketService.save(ticket);
            return ResponseEntity.ok(Map.of("success", true, "newStatus", newStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid status"));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTicketField(
            @PathVariable int id,
            @RequestParam String fieldName,
            @RequestParam String newValue) {
        try {
            Ticket ticket = ticketService.get(id);
            if ("title".equals(fieldName)) {
                ticket.setTitle(newValue);
            } else if ("description".equals(fieldName)) {
                ticket.setDescription(newValue);
            } else {
                return ResponseEntity.badRequest().body("Campo non valido");
            }
            ticketService.put(ticket); // Salva il ticket aggiornato
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'aggiornamento");
        }
    }
}


