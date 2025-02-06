package ch.supsi.webapp.tickets.Controller;

import ch.supsi.webapp.tickets.dto.TicketDTO;
import ch.supsi.webapp.tickets.model.*;
import ch.supsi.webapp.tickets.service.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
public class MainController {

    private final TicketService ticketService;
    private final UserService userService;
    private final TagService tagService;
    private final CommentService commentService;

    public MainController(TicketService service, UserService userService, TagService tagService, CommentService commentService) {
        this.ticketService = service;
        this.userService = userService;
        this.tagService = tagService;
        this.commentService = commentService;
    }

    @ModelAttribute("servletPath")
    public String contextPath(final HttpServletRequest request) {
        return request.getServletPath();
    }

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

    @GetMapping("/home-table")
    public String grid(Model model) {
        model.addAttribute("tickets", ticketService.getAll());
        return "index";
    }

    @GetMapping("/ticket/{id}")
    public String detail(@PathVariable int id, Model model, Principal principal) {

        Ticket ticket = ticketService.get(id);
        List<Comment> flatComments = commentService.getCommentsByTicket(ticket);

        if (flatComments == null) {
            flatComments = new ArrayList<>();
        }

        // Creare la struttura ad albero
        List<Comment> commentTree = buildCommentTree(flatComments);

        model.addAttribute("ticket", getTicketOrThrow(id));
        model.addAttribute("comments", commentTree);
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("watchedTickets", getCurrentUser(principal).getWatches());
        return "detail";
    }

    // Metodo per costruire l'albero dei commenti
    private List<Comment> buildCommentTree(List<Comment> comments) {
        Map<Long, Comment> commentMap = new HashMap<>();
        List<Comment> roots = new ArrayList<>();

        // Mappa tutti i commenti per ID
        for (Comment comment : comments) {
            commentMap.put(comment.getId(), comment);
        }

        // Costruisce la gerarchia
        for (Comment comment : comments) {
            if (comment.getParent() != null) {
                Comment parent = commentMap.get(comment.getParent().getId());
                if (parent != null) {
                    parent.getReplies().add(comment);
                }
            } else {
                roots.add(comment);
            }
        }

        return roots;
    }

    @GetMapping("/ticket/{id}/quickEdit")
    public String quickEdit(@PathVariable int id, Model model) {
        Ticket ticket = ticketService.get(id);
        model.addAttribute("ticket", ticket);
        return "quickEdit";
    }

    @GetMapping("/ticket/new")
    public String newPost(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("isNew", true);
        model.addAttribute("users", ticketService.getAllUsers());
        return "edit";
    }

    @PostMapping("/ticket/new")
    public String post(Ticket ticket, @RequestParam("file") MultipartFile file) throws IOException {
        ticket.setAuthor(getAuthenticatedUser());
        setAttachment(ticket, file);
        ticket = ticketService.post(ticket);
        return "redirect:/ticket/" + ticket.getId();
    }

    @GetMapping("/ticket/{id}/edit")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("ticket", getTicketOrThrow(id));
        model.addAttribute("users", ticketService.getAllUsers());
        model.addAttribute("isNew", false);
        return "edit";
    }

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

    @GetMapping(value = "/ticket/{id}/delete")
    public String delete(@PathVariable int id) {
        ticketService.delete(id);
        return "redirect:/";
    }

    @GetMapping(value = "/ticket/{id}/attachment")
    @ResponseBody
    public ResponseEntity<byte[]> getAttachmentBytes(@PathVariable int id) {
        Attachment attachment = getTicketOrThrow(id).getAttachment();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(attachment.getContentType()))
                .body(attachment.getBytes());
    }

    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = "/register")
    public String register() {
        return "register";
    }

    @PostMapping(value = "/register")
    public String register(User user) {
        ticketService.postUser(user);
        return "redirect:/login";
    }

    @GetMapping("/ticket/search")
    @ResponseBody
    public List<TicketDTO> search(@RequestParam("q") String search) {
        return ticketService.list(search).stream().map(TicketDTO::ticket2DTO).collect(Collectors.toList());
    }

    @GetMapping("/watches")
    public String getWatchedTickets(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        model.addAttribute("watchedTickets", user.getWatches());
        return "watched-tickets";
    }

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

    private Ticket getTicketOrThrow(int id) {
        if (!ticketService.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return ticketService.get(id);
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ticketService.findUserByUsername(username);
    }

    private long countByStatus(List<Ticket> tickets, String status) {
        return tickets.stream().filter(t -> t.getStatus().name().equals(status)).count();
    }

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
