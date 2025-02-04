package ch.supsi.webapp.tickets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
/**
 * Configurazione della sicurezza per l'applicazione web.
 * Questa classe definisce le regole di accesso alle risorse, le impostazioni per l'autenticazione
 * e le configurazioni per il login e il logout utilizzando Spring Security.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	/**
	 * Configura la catena di filtri di sicurezza per definire le regole di accesso.
	 * Disabilita la protezione CSRF, definisce una pagina di login personalizzata,
	 * gestisce le autorizzazioni per diverse risorse e reindirizza dopo il logout.
	 *
	 * @param http l'oggetto HttpSecurity utilizzato per configurare la sicurezza
	 * @return un oggetto SecurityFilterChain configurato
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable) // Disabilita la protezione CSRF
				.formLogin(form -> form
						.loginPage("/login") // Definisce una pagina di login personalizzata
						.failureHandler(customAuthenticationFailureHandler()) // Gestore personalizzato per errori di login
						.permitAll()) // Consente l'accesso a tutti alla pagina di login
				.logout(logout -> logout
						.logoutUrl("/logout") // URL per il logout
						.logoutSuccessUrl("/login") // Reindirizza alla pagina di login dopo il logout
						.permitAll()) // Permette a tutti di effettuare il logout
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/", "/home-table").permitAll() // Consente l'accesso alle pagine pubbliche
						.requestMatchers("/login", "/register").permitAll() // Consente l'accesso alle pagine di login e registrazione
						.requestMatchers("/ticket/search").permitAll() // Consente l'accesso alla ricerca dei ticket
						.requestMatchers("/ticket/new").authenticated() // Richiede autenticazione per la creazione di nuovi ticket
						.requestMatchers("/ticket/*/edit").hasRole("ADMIN") // Accesso riservato agli amministratori per la modifica dei ticket
						.requestMatchers("/ticket/*/delete").hasRole("ADMIN") // Accesso riservato agli amministratori per eliminare i ticket
						.requestMatchers("/ticket/**").permitAll() // Consente l'accesso ai dettagli dei ticket
						.requestMatchers("/tickets/**").permitAll() // Consente l'accesso alla lista dei ticket
						.requestMatchers("/css/**").permitAll() // Consente l'accesso ai file CSS
						.requestMatchers("/js/**").permitAll() // Consente l'accesso ai file JavaScript
						.requestMatchers("/webjars/**").permitAll() // Consente l'accesso alle dipendenze webjar
						.requestMatchers("/fonts/**").permitAll() // Consente l'accesso ai file di font
						.requestMatchers("/board").authenticated() // Richiede autenticazione per l'accesso alla board
						.requestMatchers("/milestones").permitAll()
						.requestMatchers("/ticket/*/time-spent").authenticated() // Richiede autenticazione per il tempo speso su un ticket
						.anyRequest().permitAll() // Consente l'accesso a qualsiasi altra richiesta
				)
				.build();
	}

	/**
	 * Configura un encoder per la codifica delle password.
	 * Utilizza BCrypt come algoritmo di hashing per proteggere le password.
	 *
	 * @return un'istanza di PasswordEncoder
	 */
	@Bean
	PasswordEncoder BCPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Definisce un gestore personalizzato per i fallimenti di autenticazione.
	 * Questo gestore reindirizza alla pagina di login con un messaggio di errore in caso di fallimento.
	 *
	 * @return un oggetto AuthenticationFailureHandler
	 */
	@Bean
	public AuthenticationFailureHandler customAuthenticationFailureHandler() {
		return new SimpleUrlAuthenticationFailureHandler("/login?error=true");
	}
}

