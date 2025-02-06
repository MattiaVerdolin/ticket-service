package ch.supsi.webapp.tickets.service;

import ch.supsi.webapp.tickets.model.*;
import ch.supsi.webapp.tickets.repository.RoleRepository;
import ch.supsi.webapp.tickets.repository.TicketRepository;
import ch.supsi.webapp.tickets.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder(); // Gestisce la codifica delle password.

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        // Inizializza i ruoli
        if (roleRepository.findAll().isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_USER"));
        }

        // Inizializza l'amministratore predefinito
        if (userRepository.findAll().isEmpty()) {

            User admin = new User("admin", encoder.encode("admin"),
                    "Amministratore", "Unico",
                    roleRepository.getReferenceById("ROLE_ADMIN"),
                    new ArrayList<>());

            userRepository.save(admin);
        }

        // Aggiunge ticket predefiniti
        if (ticketRepository.findAll().isEmpty()) {
            Ticket ticket1 = new Ticket();
            ticket1.setAuthor(userRepository.getReferenceById("admin"));
            ticket1.setStatus(TicketStatus.OPEN);
            ticket1.setTitle("Login non funziona");
            ticket1.setDescription("Da ieri sera non riesco più a loggarmi");
            ticket1.setType(TicketType.BUG);
            ticket1.setDate(new Date());
            ticket1.setDueDate(LocalDate.of(2025, 9, 23));
            ticket1.setAssignee(userRepository.getReferenceById("admin"));
            ticket1.setEstimate(20);
            ticket1.setTimeSpent(0);
            post(ticket1);

            Ticket ticket2 = new Ticket();
            ticket2.setAuthor(userRepository.getReferenceById("admin"));
            ticket2.setStatus(TicketStatus.OPEN);
            ticket2.setTitle("Schermata bianca");
            ticket2.setDescription("Quando apro l'applicativo ho una schermata bianca");
            ticket2.setType(TicketType.BUG);
            ticket2.setDate(new Date());
            ticket2.setDueDate(LocalDate.of(2025, 6, 15));
            ticket2.setAssignee(userRepository.getReferenceById("admin"));
            ticket2.setEstimate(30);
            ticket2.setTimeSpent(13);
            post(ticket2);
        }
    }

    // Metodi CRUD per i ticket

    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByTag(String tagName) {
        return ticketRepository.findByTags_Name(tagName);
    }

    public Ticket get(int id) {
        return ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public Ticket post(Ticket ticket) {
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setDate(new Date());
        return ticketRepository.save(ticket);
    }

    public Ticket put(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public boolean exists(int id) {
        return ticketRepository.existsById(id);
    }

    @Transactional
    public boolean delete(int ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Rimuove il ticket dai "watches" degli utenti
        List<User> usersWatching = userRepository.findAllByWatchesContains(ticket);
        for (User user : usersWatching) {
            user.getWatches().remove(ticket);
            userRepository.save(user);
        }

        // Elimina il ticket
        ticketRepository.delete(ticket);
        return true;
    }

    // Metodi per la gestione degli utenti

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public void postUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(roleRepository.findById("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found")));
        userRepository.save(user);
    }

    public List<Ticket> list(String search) {
        return ticketRepository.findTop5ByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByDateDesc(search, search);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void save(Ticket ticket) {
        ticketRepository.save(ticket);
    }
}
