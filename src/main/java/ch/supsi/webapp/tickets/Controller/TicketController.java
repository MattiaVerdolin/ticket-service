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
/**
 * TicketController è un controller REST che espone endpoint HTTP per gestire i ticket.
 * Fornisce metodi per le operazioni CRUD (Create, Read, Update, Delete) sui ticket e
 * permette anche di aggiungere ticket alla lista dei "watches" di un utente.
 * Utilizza il TicketService e UserService per interagire con i dati e restituisce
 * risposte in formato JSON, ideale per applicazioni frontend o client API.
 */
@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;
    private final MilestoneService milestoneService;

    public TicketController(TicketService service, UserService userService, MilestoneService milestoneService) {
        this.ticketService = service;
        this.userService = userService;
        this.milestoneService = milestoneService;
    }

    /**
     * Recupera tutti i ticket dal database.
     * Converte gli oggetti Ticket in DTO per esporre solo i dati necessari.
     * @return Una lista di TicketDTO contenente tutti i ticket esistenti.
     */
    @GetMapping("")
    public List<TicketDTO> list() {
        return ticketService.getAll().stream()
                .map(ticket -> TicketDTO.ticket2DTO(ticket))
                .collect(Collectors.toList());
    }

    /**
     * Recupera un ticket specifico per ID.
     * Se il ticket non esiste, restituisce uno stato HTTP 404.
     * @param id L'ID del ticket da recuperare.
     * @return Un oggetto ResponseEntity contenente il TicketDTO o uno stato 404.
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<TicketDTO> get(@PathVariable int id) {
        if (!ticketService.exists(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Ticket ticket = ticketService.get(id);
        return ticket != null
                ? new ResponseEntity<>(TicketDTO.ticket2DTO(ticket), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Crea un nuovo ticket.
     * Riceve i dati come JSON, salva il ticket e restituisce il ticket creato.
     * @param ticket Il ticket inviato nel corpo della richiesta.
     * @return Un ResponseEntity contenente il TicketDTO creato e uno stato HTTP 201.
     */
    @PostMapping(value = "")
    public ResponseEntity<TicketDTO> post(@RequestBody Ticket ticket) {
        ticket = ticketService.post(ticket);
        return new ResponseEntity<>(TicketDTO.ticket2DTO(ticket), HttpStatus.CREATED);
    }

    /**
     * Aggiorna un ticket esistente.
     * Controlla se il ticket esiste prima di aggiornarlo.
     * @param id L'ID del ticket da aggiornare.
     * @param ticket I dati aggiornati inviati nel corpo della richiesta.
     * @return Un ResponseEntity contenente il TicketDTO aggiornato e uno stato HTTP 200.
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<TicketDTO> put(@PathVariable int id, @RequestBody Ticket ticket) {
        if (!ticketService.exists(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        ticket.setId(id);
        ticketService.put(ticket);
        return new ResponseEntity<>(TicketDTO.ticket2DTO(ticket), HttpStatus.OK);
    }

    /**
     * Elimina un ticket specifico.
     * Se il ticket non esiste, restituisce uno stato HTTP 404.
     * @param id L'ID del ticket da eliminare.
     * @return Un ResponseEntity contenente lo stato dell'operazione (successo o fallimento).
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Success> delete(@PathVariable int id) {
        if (!ticketService.exists(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        boolean remove = ticketService.delete(id);
        return new ResponseEntity<>(new Success(remove), HttpStatus.OK);
    }

    /**
     * Recupera i dettagli di un ticket specifico in formato DTO.
     * @param id L'ID del ticket da recuperare.
     * @return Un ResponseEntity contenente il TicketDTO.
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<TicketDTO> getTicketDetails(@PathVariable Integer id) {
        Ticket ticket = ticketService.get(id);
        TicketDTO ticketDTO = TicketDTO.ticket2DTO(ticket);
        return ResponseEntity.ok(ticketDTO);
    }

    /**
     * Aggiunge un ticket alla lista dei "watches" dell'utente autenticato.
     * Se il ticket non esiste, restituisce uno stato HTTP 404.
     * @param id L'ID del ticket da aggiungere.
     * @param principal L'utente autenticato.
     * @return Un ResponseEntity contenente un messaggio di conferma o errore.
     */
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
}


