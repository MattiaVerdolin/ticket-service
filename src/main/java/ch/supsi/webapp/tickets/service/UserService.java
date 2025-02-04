package ch.supsi.webapp.tickets.service;

import ch.supsi.webapp.tickets.model.User;
import ch.supsi.webapp.tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserService gestisce la logica applicativa relativa agli utenti.
 * Funziona come intermediario tra i controller e il repository degli utenti,
 * fornendo metodi per accedere e gestire i dati relativi agli utenti.
 *
 * Annotazioni:
 * - `@Service`: Indica che questa classe è un componente di servizio in Spring,
 *   rendendola disponibile per l'iniezione automatica nei controller o in altri servizi.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Recupera un utente dal database in base al nome utente.
     *
     * @param username Il nome utente da cercare.
     * @return L'utente corrispondente, o null se non trovato.
     */
    public User getByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    /**
     * Salva un utente nel database.
     * Può essere utilizzato sia per creare un nuovo utente che per aggiornare un utente esistente.
     *
     * @param user L'oggetto User da salvare.
     */
    public void save(User user) {
        userRepository.save(user);
    }
}
