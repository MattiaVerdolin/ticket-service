package ch.supsi.webapp.tickets.config;

import ch.supsi.webapp.tickets.model.User;
import ch.supsi.webapp.tickets.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Classe che implementa il servizio personalizzato per il caricamento dei dettagli utente.
 * Questa classe estende UserDetailsService di Spring Security ed è responsabile
 * di recuperare i dettagli dell'utente dal servizio TicketService, utilizzati durante
 * il processo di autenticazione.
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

	// Servizio utilizzato per interagire con il repository degli utenti
	private final TicketService ticketService;

	/**
	 * Costruttore per iniettare il servizio TicketService.
	 * @param ticketService servizio per la gestione dei dati relativi ai ticket e agli utenti
	 */
	public CustomUserDetailService(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	/**
	 * Metodo che carica i dettagli di un utente in base al suo username.
	 * Viene utilizzato da Spring Security per autenticare gli utenti durante il login.
	 * @param username il nome utente per cui recuperare i dettagli
	 * @return un'istanza di UserDetails che rappresenta l'utente autenticato
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Recupera l'utente dal servizio utilizzando lo username fornito
		User user = ticketService.findUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("L'utente inserito non esiste");
		}

		// Crea una lista di autorizzazioni basata sul ruolo dell'utente
		List<GrantedAuthority> auth = AuthorityUtils.createAuthorityList(user.getRole().getName());

		// Restituisce un oggetto User compatibile con Spring Security
		return new org.springframework.security.core.userdetails.User(
				username, user.getPassword(),
				true, true, true, true,
				auth);
	}
}

