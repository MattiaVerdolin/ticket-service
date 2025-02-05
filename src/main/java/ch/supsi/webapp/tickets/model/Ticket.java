package ch.supsi.webapp.tickets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * La classe Ticket rappresenta un'entità che descrive un ticket all'interno del sistema.
 * Ogni ticket include informazioni come titolo, descrizione, autore, assegnatario,
 * stato, tipo, date e progressi relativi alla gestione del ticket.
 *
 * Annotazioni:
 * - `@Entity`: Indica che questa classe è un'entità JPA mappata a una tabella del database.
 * - `@Id` e `@GeneratedValue`: Specificano che il campo `id` è la chiave primaria e sarà generata automaticamente.
 * - `@Getter`, `@Setter`: Generano automaticamente i getter e setter per i campi della classe.
 * - `@AllArgsConstructor`: Genera un costruttore con tutti i campi.
 * - `@NoArgsConstructor`: Genera un costruttore vuoto.
 * - `@EqualsAndHashCode(of = {"id"})`: Genera metodi `equals` e `hashCode` basati sul campo `id`.
 * - `@ToString`: Genera il metodo `toString`, escludendo i campi annotati con `@JsonIgnore`.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
@Entity
public class Ticket {

	/**
	 * Identificativo univoco del ticket.
	 * Generato automaticamente dal database.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	/**
	 * Titolo del ticket, che rappresenta un breve riepilogo.
	 */
	private String title;

	/**
	 * Descrizione dettagliata del problema o attività associata al ticket.
	 * Memorizzata come `TEXT` nel database per supportare grandi quantità di testo.
	 */
	@Column(columnDefinition = "TEXT")
	private String description;

	/**
	 * Data di creazione del ticket.
	 */
	private Date date;

	/**
	 * Utente che ha creato il ticket.
	 * È una relazione @ManyToOne poiché un utente può creare più ticket.
	 */
	@ManyToOne
	private User author;

	/**
	 * Stato corrente del ticket, ad esempio OPEN, IN_PROGRESS, DONE, CLOSED.
	 * Memorizzato come stringa grazie a @Enumerated(EnumType.STRING).
	 */
	@Enumerated(EnumType.STRING)
	private TicketStatus status;

	/**
	 * Tipo del ticket, ad esempio BUG, TASK, STORY.
	 * Anch'esso memorizzato come stringa.
	 */
	@Enumerated(EnumType.STRING)
	private TicketType type;

	/**
	 * Allegato associato al ticket.
	 * È un oggetto embeddable che include il contenuto del file e i relativi metadati.
	 * Annotato con @JsonIgnore per escluderlo dalle risposte JSON.
	 */
	@Embedded
	@JsonIgnore
	private Attachment attachment;

	// Nuovi campi per esame di prova

	/**
	 * Utente assegnato al ticket.
	 * Un ticket può essere assegnato a un solo utente, ma un utente può avere più ticket assegnati.
	 */
	@ManyToOne
	private User assignee;

	/**
	 * Data di scadenza del ticket.
	 * Indica il termine entro cui il ticket deve essere completato.
	 */
	private LocalDate dueDate;

	/**
	 * Tempo stimato (in ore) necessario per completare il ticket.
	 */
	private int estimate;

	/**
	 * Tempo effettivamente speso (in ore) sul ticket.
	 */
	private int timeSpent;

	/**
	 * Progresso del ticket in percentuale.
	 * Calcolato in base al tempo stimato e al tempo speso.
	 */
	private float progress;

	@ManyToOne
	@JoinColumn(name = "milestone_id")
	private Milestone milestone;

	@ManyToMany
	@JoinTable(
			name = "ticket_tags",
			joinColumns = @JoinColumn(name = "ticket_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id")
	)
	private List<Tag> tags = new ArrayList<>();

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<Comment> comments = new ArrayList<>();

	public void addTag(Tag tag) {
		if (!tags.contains(tag)) {
			tags.add(tag);
			tag.getTickets().add(this);
		}
	}

	public void removeTag(Tag tag) {
		tags.remove(tag);
		tag.getTickets().remove(this);
	}

}
