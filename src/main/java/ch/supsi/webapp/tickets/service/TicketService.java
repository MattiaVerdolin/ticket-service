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

/**
 * TicketService gestisce la logica applicativa per la gestione di utenti, ruoli e ticket.
 * Funziona come ponte tra i controller e i repository, fornendo metodi per accedere
 * ai dati e applicare regole di business.
 *
 * Funzionalità principali:
 * - Gestione CRUD per ticket e utenti.
 * - Inizializzazione automatica di dati predefiniti (amministratore, ruoli, ticket).
 * - Supporto per la rimozione di ticket e aggiornamento delle relazioni tra utenti e ticket.
 */
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder(); // Gestisce la codifica delle password.

    /**
     * Costruttore per iniettare i repository necessari.
     *
     * @param ticketRepository Il repository per la gestione dei ticket.
     * @param userRepository Il repository per la gestione degli utenti.
     * @param roleRepository Il repository per la gestione dei ruoli.
     */
    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Metodo di inizializzazione eseguito automaticamente all'avvio dell'applicazione.
     * - Crea ruoli predefiniti (`ROLE_ADMIN`, `ROLE_USER`).
     * - Crea un utente amministratore predefinito.
     * - Aggiunge due ticket di esempio.
     */
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

    /**
     * Recupera tutti i ticket.
     *
     * @return Una lista di tutti i ticket nel database.
     */
    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByTag(String tagName) {
        return ticketRepository.findByTags_Name(tagName);
    }

    /**
     * Recupera un ticket specifico per ID.
     *
     * @param id L'ID del ticket da recuperare.
     * @return Il ticket corrispondente all'ID fornito.
     */
    public Ticket get(int id) {
        return ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    /**
     * Crea un nuovo ticket.
     * Imposta lo stato come OPEN e la data di creazione come quella attuale.
     *
     * @param ticket Il ticket da creare.
     * @return Il ticket salvato.
     */
    public Ticket post(Ticket ticket) {
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setDate(new Date());
        return ticketRepository.save(ticket);
    }

    /**
     * Aggiorna un ticket esistente.
     *
     * @param ticket Il ticket con i dati aggiornati.
     * @return Il ticket aggiornato.
     */
    public Ticket put(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    /**
     * Verifica se un ticket esiste nel database.
     *
     * @param id L'ID del ticket da verificare.
     * @return true se il ticket esiste, false altrimenti.
     */
    public boolean exists(int id) {
        return ticketRepository.existsById(id);
    }

    /**
     * Elimina un ticket specifico.
     * Rimuove anche il ticket dalla lista dei "watches" di tutti gli utenti.
     *
     * @param ticketId L'ID del ticket da eliminare.
     * @return true se l'eliminazione ha successo.
     */
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

    /**
     * Recupera un utente tramite il suo username.
     *
     * @param username Lo username dell'utente.
     * @return L'utente corrispondente.
     */
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    /**
     * Crea un nuovo utente con ruolo predefinito USER.
     * La password viene codificata prima di essere salvata.
     *
     * @param user L'utente da creare.
     */
    public void postUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(roleRepository.findById("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found")));
        userRepository.save(user);
    }

    /**
     * Cerca i primi 5 ticket che contengono una stringa nel titolo o nella descrizione.
     *
     * @param search La stringa da cercare.
     * @return Una lista di ticket corrispondenti.
     */
    public List<Ticket> list(String search) {
        return ticketRepository.findTop5ByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByDateDesc(search, search);
    }

    /**
     * Recupera tutti gli utenti dal database.
     *
     * @return Una lista di tutti gli utenti.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void save(Ticket ticket) {
        ticketRepository.save(ticket);
    }
}
