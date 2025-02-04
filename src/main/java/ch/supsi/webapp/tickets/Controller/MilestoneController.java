package ch.supsi.webapp.tickets.Controller;

import ch.supsi.webapp.tickets.model.Milestone;
import ch.supsi.webapp.tickets.model.MilestoneStatus;
import ch.supsi.webapp.tickets.model.Ticket;
import ch.supsi.webapp.tickets.service.MilestoneService;
import ch.supsi.webapp.tickets.service.TicketService;
import ch.supsi.webapp.tickets.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/milestones")
public class MilestoneController {

    private final MilestoneService milestoneService;
    private final TicketService ticketService;
    private final UserService userService;

    public MilestoneController(MilestoneService milestoneService, TicketService ticketService, UserService userService) {
        this.milestoneService = milestoneService;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping
    public String listMilestones(Model model) {

        for (Milestone milestone : milestoneService.getAll()) {
            milestone.updateProgress();
            milestoneService.save(milestone);
        }

        model.addAttribute("milestones", milestoneService.getAll());
        return "milestones";
    }

    @GetMapping("/new")
    public String newMilestone(Model model) {
        model.addAttribute("milestone", new Milestone());
        model.addAttribute("isNew", true);
        return "create-milestone";
    }

    @PostMapping("/new")
    public String createMilestone(@ModelAttribute Milestone milestone, Principal principal) {
        milestone.setAuthor(userService.getByUsername(principal.getName()));
        milestoneService.post(milestone);
        return "redirect:/milestones";
    }

    @GetMapping("/{id}/add-ticket")
    public String addTicketForm(@PathVariable Long id, Model model) {
        Milestone milestone = milestoneService.getMilestoneById(id);
        List<Ticket> tickets = ticketService.getAll();
        model.addAttribute("milestone", milestone);
        model.addAttribute("tickets", tickets);
        return "add-ticket";
    }


    @PostMapping("/{id}/add-ticket")
    public String addTicketToMilestone(@PathVariable Long id, @RequestParam int ticketId) {
        Milestone milestone = milestoneService.getMilestoneById(id);
        Ticket ticket = ticketService.get(ticketId);
        if (milestone != null && ticket != null) {
            milestone.addTicket(ticket);
            ticket.setMilestone(milestone);
            ticketService.save(ticket);
            milestoneService.save(milestone);
        }
        return "redirect:/milestones";
    }

    @PostMapping("/{id}/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeMilestone(@PathVariable Long id) {
        Milestone milestone = milestoneService.getMilestoneById(id);

        if (milestone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Milestone not found"));
        }

        milestone.setStatus(MilestoneStatus.COMPLETED);
        milestoneService.save(milestone);

        return ResponseEntity.ok(Map.of("success", true, "newStatus", "COMPLETED"));
    }
}
