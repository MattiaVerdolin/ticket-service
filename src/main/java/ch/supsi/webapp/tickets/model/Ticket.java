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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	private Date date;

	@ManyToOne
	private User author;

	@Enumerated(EnumType.STRING)
	private TicketStatus status;

	@Enumerated(EnumType.STRING)
	private TicketType type;

	@Embedded
	@JsonIgnore
	private Attachment attachment;

	@ManyToOne
	private User assignee;

	private LocalDate dueDate;

	private int estimate;

	private int timeSpent;

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
}
