package ch.supsi.webapp.tickets.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    private String username;

    private String password;

    private String firstname;

    private String lastname;

    @ManyToOne
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_watches",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ticket_id")
    )
    private List<Ticket> watches = new ArrayList<>();

    public User(String username) {
        this.username = username;
    }
}
