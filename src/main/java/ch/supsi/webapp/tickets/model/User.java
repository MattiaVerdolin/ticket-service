package ch.supsi.webapp.tickets.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe User rappresenta un utente del sistema.
 * Ogni utente ha credenziali di accesso, un ruolo e può monitorare i ticket.
 *
 * Annotazioni:
 * - `@Entity`: Indica che questa classe è un'entità JPA e sarà mappata a una tabella nel database.
 * - `@Id`: Specifica il campo `username` come chiave primaria.
 * - `@Data`: Genera automaticamente getter, setter, metodi `toString`, `equals`, e `hashCode`.
 * - `@AllArgsConstructor`: Crea un costruttore con tutti i campi.
 * - `@NoArgsConstructor`: Crea un costruttore vuoto.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    /**
     * Nome utente unico utilizzato come chiave primaria.
     */
    @Id
    private String username;

    /**
     * Password dell'utente per l'autenticazione.
     */
    private String password;

    /**
     * Nome dell'utente.
     */
    private String firstname;

    /**
     * Cognome dell'utente.
     */
    private String lastname;

    /**
     * Ruolo dell'utente (ad esempio, ROLE_ADMIN, ROLE_USER).
     * È una relazione @ManyToOne poiché più utenti possono avere lo stesso ruolo.
     */
    @ManyToOne
    private Role role;

    /**
     * Elenco dei ticket che l'utente sta monitorando.
     * Relazione @ManyToMany che utilizza una tabella di join chiamata "user_watches".
     */
    @ManyToMany
    @JoinTable(
            name = "user_watches",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ticket_id")
    )
    private List<Ticket> watches = new ArrayList<>();

    /**
     * Costruttore parziale che inizializza solo il nome utente.
     * Utile per creare oggetti User con un identificativo minimo.
     * @param username Nome utente univoco.
     */
    public User(String username) {
        this.username = username;
    }
}
