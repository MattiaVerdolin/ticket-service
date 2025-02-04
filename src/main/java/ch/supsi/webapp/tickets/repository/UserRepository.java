package ch.supsi.webapp.tickets.repository;

import ch.supsi.webapp.tickets.model.Ticket;
import ch.supsi.webapp.tickets.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * UserRepository è un'interfaccia che estende JpaRepository per gestire
 * le operazioni CRUD relative alla classe User.
 * Fornisce metodi personalizzati per interrogare il database, specifici
 * per la gestione degli utenti e dei ticket monitorati.
 *
 * Metodi principali:
 * - `findUserByUsername`: Recupera un utente specifico tramite il nome utente.
 * - `findAllByWatchesContains`: Recupera tutti gli utenti che stanno monitorando
 *   uno specifico ticket.
 */
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Recupera un utente in base al suo username.
     * @param username Il nome utente da cercare.
     * @return L'utente corrispondente, o null se non trovato.
     */
    User findUserByUsername(String username);

    /**
     * Recupera tutti gli utenti che stanno monitorando un determinato ticket.
     * @param ticket Il ticket da verificare nei "watches" degli utenti.
     * @return Una lista di utenti che stanno monitorando il ticket specificato.
     */
    List<User> findAllByWatchesContains(Ticket ticket);
}
