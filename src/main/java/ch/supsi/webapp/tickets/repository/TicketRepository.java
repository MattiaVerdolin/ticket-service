package ch.supsi.webapp.tickets.repository;

import ch.supsi.webapp.tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * TicketRepository è un'interfaccia che estende JpaRepository per gestire
 * le operazioni CRUD relative alla classe Ticket.
 * Fornisce metodi personalizzati per interrogare il database, in particolare
 * per cercare ticket con criteri specifici.
 *
 * Metodi principali:
 * - Metodi CRUD forniti automaticamente da JpaRepository.
 * - `findTop5ByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByDateDesc`:
 *   Cerca i primi 5 ticket che contengono una stringa nel titolo o nella descrizione,
 *   ordinati per data in ordine decrescente.
 */
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    /**
     * Recupera i primi 5 ticket che contengono la stringa specificata nel titolo
     * o nella descrizione (ignorando maiuscole e minuscole).
     * Ordina i risultati per data in ordine decrescente.
     *
     * @param title La stringa da cercare nel titolo dei ticket.
     * @param description La stringa da cercare nella descrizione dei ticket.
     * @return Una lista di massimo 5 ticket che soddisfano i criteri di ricerca.
     */
    List<Ticket> findTop5ByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByDateDesc(String title, String description);
}
