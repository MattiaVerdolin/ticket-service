package ch.supsi.webapp.tickets.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * La classe Role rappresenta un ruolo utente nel sistema.
 * I ruoli vengono utilizzati per definire i permessi e le autorizzazioni
 * degli utenti, consentendo di gestire l'accesso a specifiche funzionalità
 * dell'applicazione.
 *
 * Annotazioni:
 * - `@Entity`: Indica che questa classe è una entità JPA e sarà mappata a una tabella del database.
 * - `@Id`: Specifica il campo `name` come chiave primaria.
 * - `@Data`: Genera automaticamente getter, setter, metodi `toString`, `equals`, e `hashCode`.
 * - `@AllArgsConstructor`: Crea un costruttore con tutti i campi.
 * - `@NoArgsConstructor`: Crea un costruttore senza argomenti.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role {

    /**
     * Nome del ruolo, ad esempio "ROLE_ADMIN" o "ROLE_USER".
     * Questo campo funge da chiave primaria nel database.
     */
    @Id
    private String name;
}
