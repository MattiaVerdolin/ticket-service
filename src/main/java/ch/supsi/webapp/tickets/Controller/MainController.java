package ch.supsi.webapp.tickets.Controller;

import ch.supsi.webapp.tickets.dto.TicketDTO;
import ch.supsi.webapp.tickets.model.*;
import ch.supsi.webapp.tickets.service.MilestoneService;
import ch.supsi.webapp.tickets.service.TagService;
import ch.supsi.webapp.tickets.service.TicketService;
import ch.supsi.webapp.tickets.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller principale dell'applicazione che gestisce le richieste HTTP per la gestione dei ticket.
 * Questa classe include metodi per visualizzare, creare, modificare, eliminare e cercare ticket.
 * Inoltre, offre funzionalità per la gestione degli allegati e dei ticket "guardati" dagli utenti.
 */
@Controller
public class MainController {

    private final TicketService ticketService;
    private final UserService userService;
    private final TagService tagService;

    /**
     * Costruttore della classe MainController.
     * Inizializza i servizi per la gestione dei ticket e degli utenti.
     * @param service il servizio per la gestione dei ticket
     * @param userService il servizio per la gestione degli utenti
     */
    public MainController(TicketService service, UserService userService, TagService tagService) {
        this.ticketService = service;
        this.userService = userService;
        this.tagService = tagService;
    }

    /**
     * Fornisce il percorso servlet corrente come attributo del modello.
     * Questo metodo è annotato con @ModelAttribute, il che significa che il valore restituito
     * viene aggiunto automaticamente al modello e reso disponibile nei template Thymeleaf.
     *
     * Utilizzabile, ad esempio, per evidenziare dinamicamente il percorso attivo
     * nell'interfaccia utente, come la navigazione del menu.
     *
     * @param request L'oggetto HttpServletRequest che rappresenta la richiesta HTTP corrente.
     * @return Il percorso servlet corrente della richiesta.
     */
    @ModelAttribute("servletPath")
    public String contextPath(final HttpServletRequest request) {
        return request.getServletPath();
    }

    /**
     * Gestisce la richiesta per la homepage.
     * Recupera la lista di tutti i ticket e l'utente autenticato, passando i dati al modello.
     * @param model Il modello per passare dati alla vista.
     * @param principal Informazioni sull'utente autenticato.
     * @return Il nome del template Thymeleaf per la homepage.
     */
    @GetMapping("/")
    public String index(@RequestParam(value = "tag", required = false) String tag, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        List<Ticket> tickets = (tag != null && !tag.isEmpty())
                ? ticketService.getTicketsByTag(tag)
                : ticketService.getAll();

        model.addAttribute("tickets", tickets);
        model.addAttribute("user", getCurrentUser(principal));

        return "index-card";
    }

    /**
     * Gestisce la visualizzazione del "board" con il progresso dei ticket e il conteggio per stato.
     * @param model Il modello per passare dati alla vista.
     * @return Il nome del template Thymeleaf per il board.
     */
    @GetMapping("/board")
    public String board(Model model) {
        List<Ticket> tickets = ticketService.getAll();

        for (Ticket ticket : tickets) {
            if (ticket.getEstimate() > 0) {
                ticket.setProgress(((float) ticket.getTimeSpent() / ticket.getEstimate()) * 100);
            } else {
                ticket.setProgress(0);
            }
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("openCount", countByStatus(tickets, "OPEN"));
        model.addAttribute("inProgressCount", countByStatus(tickets, "IN_PROGRESS"));
        model.addAttribute("doneCount", countByStatus(tickets, "DONE"));
        model.addAttribute("closedCount", countByStatus(tickets, "CLOSED"));

        return "board";
    }

    /**
     * Gestisce la visualizzazione dei ticket in formato tabellare.
     * @param model Il modello per passare dati alla vista.
     * @return Il nome del template Thymeleaf per la visualizzazione a tabella.
     */
    @GetMapping("/home-table")
    public String grid(Model model) {
        model.addAttribute("tickets", ticketService.getAll());
        return "index";
    }

    /**
     * Mostra i dettagli di un ticket specifico.
     * @param id L'ID del ticket da visualizzare.
     * @param model Il modello per passare dati alla vista.
     * @param principal Informazioni sull'utente autenticato.
     * @return Il nome del template Thymeleaf per i dettagli del ticket.
     */
    @GetMapping("/ticket/{id}")
    public String detail(@PathVariable int id, Model model, Principal principal) {
        model.addAttribute("ticket", getTicketOrThrow(id));
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("watchedTickets", getCurrentUser(principal).getWatches());
        return "detail";
    }

    /**
     * Mostra il form per creare un nuovo ticket.
     * @param model Il modello per passare dati alla vista.
     * @return Il nome del template Thymeleaf per il form di creazione.
     */
    @GetMapping("/ticket/new")
    public String newPost(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("isNew", true);
        model.addAttribute("users", ticketService.getAllUsers());
        return "edit";
    }

    /**
     * Gestisce la creazione di un nuovo ticket.
     * @param ticket Il ticket inviato tramite il form.
     * @param file L'allegato inviato con il form.
     * @return Una redirezione alla pagina dei dettagli del ticket.
     * @throws IOException Se si verifica un problema con l'allegato.
     */
    @PostMapping("/ticket/new")
    public String post(Ticket ticket, @RequestParam("file") MultipartFile file) throws IOException {
        ticket.setAuthor(getAuthenticatedUser());
        setAttachment(ticket, file);
        ticket = ticketService.post(ticket);
        return "redirect:/ticket/" + ticket.getId();
    }

    /**
     * Mostra il form per modificare un ticket esistente.
     * @param id L'ID del ticket da modificare.
     * @param model Il modello per passare dati alla vista.
     * @return Il nome del template Thymeleaf per il form di modifica.
     */
    @GetMapping("/ticket/{id}/edit")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("ticket", getTicketOrThrow(id));
        model.addAttribute("users", ticketService.getAllUsers());
        model.addAttribute("isNew", false);
        return "edit";
    }

    /**
     * Gestisce l'aggiornamento di un ticket esistente.
     * @param id L'ID del ticket da aggiornare.
     * @param updatedTicket Il ticket con i dati aggiornati.
     * @param file L'allegato aggiornato.
     * @return Una redirezione alla pagina dei dettagli del ticket.
     * @throws IOException Se si verifica un problema con l'allegato.
     */
    @PostMapping("/ticket/{id}/edit")
    public String put(@PathVariable int id, Ticket updatedTicket, @RequestParam("file") MultipartFile file) throws IOException {
        Ticket ticket = getTicketOrThrow(id);
        ticket.setTitle(updatedTicket.getTitle());
        ticket.setType(updatedTicket.getType());
        ticket.setStatus(updatedTicket.getStatus());
        ticket.setDescription(updatedTicket.getDescription());
        ticket.setDueDate(updatedTicket.getDueDate());
        ticket.setEstimate(updatedTicket.getEstimate());
        ticket.setAssignee(updatedTicket.getAssignee());
        setAttachment(ticket, file);
        ticketService.put(ticket);
        return "redirect:/ticket/" + id;
    }

    /**
     * Aggiorna il tempo speso su un ticket.
     * @param id L'ID del ticket da aggiornare.
     * @param timeSpent Il nuovo valore di tempo speso.
     * @return Una redirezione alla pagina dei dettagli del ticket.
     */
    @PostMapping("/ticket/{id}/update")
    public String updateTimeSpent(@PathVariable int id, @RequestParam int timeSpent) {
        Ticket ticket = getTicketOrThrow(id);

        if (timeSpent <= ticket.getEstimate()) {
            ticket.setTimeSpent(timeSpent);
            ticketService.put(ticket);

        } else {
            System.err.println("time spent > time estimate");
        }
        return "redirect:/ticket/" + id;
    }

    /**
     * Elimina un ticket esistente.
     * @param id L'ID del ticket da eliminare.
     * @return Una redirezione alla homepage.
     */
    @GetMapping(value = "/ticket/{id}/delete")
    public String delete(@PathVariable int id) {
        ticketService.delete(id);
        return "redirect:/";
    }

    /**
     * Restituisce i byte di un allegato associato a un ticket.
     * @param id L'ID del ticket con l'allegato.
     * @return Una risposta contenente i byte dell'allegato.
     */
    @GetMapping(value = "/ticket/{id}/attachment")
    @ResponseBody
    public ResponseEntity<byte[]> getAttachmentBytes(@PathVariable int id) {
        Attachment attachment = getTicketOrThrow(id).getAttachment();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(attachment.getContentType()))
                .body(attachment.getBytes());
    }

    /**
     * Mostra la pagina di login.
     * @return Il nome del template Thymeleaf per il login.
     */
    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    /**
     * Mostra la pagina di registrazione.
     * @return Il nome del template Thymeleaf per la registrazione.
     */
    @GetMapping(value = "/register")
    public String register() {
        return "register";
    }

    /**
     * Gestisce la registrazione di un nuovo utente.
     * @param user Il nuovo utente registrato.
     * @return Una redirezione alla pagina di login.
     */
    @PostMapping(value = "/register")
    public String register(User user) {
        ticketService.postUser(user);
        return "redirect:/login";
    }

    /**
     * Cerca ticket in base a una stringa di ricerca.
     * @param search La stringa di ricerca.
     * @return Una lista di TicketDTO corrispondenti ai risultati della ricerca.
     */
    @GetMapping("/ticket/search")
    @ResponseBody
    public List<TicketDTO> search(@RequestParam("q") String search) {
        return ticketService.list(search).stream().map(TicketDTO::ticket2DTO).collect(Collectors.toList());
    }

    /**
     * Mostra i ticket "guardati" dall'utente autenticato.
     * @param model Il modello per passare dati alla vista.
     * @param principal Informazioni sull'utente autenticato.
     * @return Il nome del template Thymeleaf per i ticket guardati.
     */
    @GetMapping("/watches")
    public String getWatchedTickets(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        model.addAttribute("watchedTickets", user.getWatches());
        return "watched-tickets";
    }

    /**
     * Restituisce l'utente autenticato.
     * @param principal Informazioni sull'utente autenticato.
     * @return L'utente autenticato o null se non disponibile.
     */
    @ModelAttribute("user")
    public User getCurrentUser(Principal principal) {
        if (principal != null) {
            return userService.getByUsername(principal.getName());
        }
        return null;
    }

    @ModelAttribute("allTags")
    public List<String> getAllTags() {
        return tagService.getAllTags();
    }

    /**
     * Recupera un ticket tramite ID, lanciando un'eccezione se non esiste.
     * @param id L'ID del ticket da recuperare.
     * @return Il ticket recuperato.
     */
    private Ticket getTicketOrThrow(int id) {
        if (!ticketService.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return ticketService.get(id);
    }

    /**
     * Recupera l'utente autenticato dal contesto di sicurezza.
     * @return L'utente autenticato.
     */
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ticketService.findUserByUsername(username);
    }

    /**
     * Conta i ticket in base allo stato specificato.
     * @param tickets La lista di ticket da filtrare.
     * @param status Lo stato da conteggiare.
     * @return Il numero di ticket con lo stato specificato.
     */
    private long countByStatus(List<Ticket> tickets, String status) {
        return tickets.stream().filter(t -> t.getStatus().name().equals(status)).count();
    }

    /**
     * Imposta l'allegato per un ticket.
     * @param ticket Il ticket a cui associare l'allegato.
     * @param attachment Il file caricato dall'utente.
     * @throws IOException Se si verifica un problema con il file.
     */
    private static void setAttachment(Ticket ticket, MultipartFile attachment) throws IOException {
        if (!attachment.isEmpty()) {
            ticket.setAttachment(Attachment.builder()
                    .bytes(attachment.getBytes())
                    .name(attachment.getOriginalFilename())
                    .contentType(attachment.getContentType())
                    .size(attachment.getSize())
                    .build());
        }
    }
}
