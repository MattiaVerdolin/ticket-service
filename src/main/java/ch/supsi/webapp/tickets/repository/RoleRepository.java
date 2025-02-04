package ch.supsi.webapp.tickets.repository;

import ch.supsi.webapp.tickets.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RoleRepository è un'interfaccia che estende JpaRepository per gestire
 * le operazioni CRUD relative alla classe Role.
 *
 * Funzionalità principali:
 * - Eredita tutti i metodi CRUD predefiniti da JpaRepository.
 * - Permette di interagire con i ruoli memorizzati nel database.
 *
 * Dettagli:
 * - La chiave primaria della classe Role è di tipo String (`name`).
 * - È possibile aggiungere metodi personalizzati per query specifiche sui ruoli.
 */
public interface RoleRepository extends JpaRepository<Role, String> { }
