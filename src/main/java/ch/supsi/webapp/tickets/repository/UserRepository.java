package ch.supsi.webapp.tickets.repository;

import ch.supsi.webapp.tickets.model.Ticket;
import ch.supsi.webapp.tickets.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    User findUserByUsername(String username);
    List<User> findAllByWatchesContains(Ticket ticket);
}
