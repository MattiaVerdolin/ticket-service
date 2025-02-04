package ch.supsi.webapp.tickets.repository;

import ch.supsi.webapp.tickets.model.Ticket;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Repository
public class InMemoryRepository {
    private List<Ticket> tickets = new ArrayList<>();

    private Random randomGenerator = new Random();

    public List<Ticket> findAll() {
        return tickets;
    }

    public Optional<Ticket> getOne(@PathVariable int id) {
        tickets.forEach(t-> System.out.println(t));
        Optional<Ticket> first = tickets.stream().filter(bp -> bp.getId() == id).findFirst();
        return first;
    }

    public Ticket create(Ticket ticket){
        ticket.setId(randomGenerator.nextInt(100000));
        tickets.add(ticket);
        return ticket;
    }

    public Ticket update(int id, Ticket ticket){
        Optional<Ticket> dbTicket = getOne(id);
        if (!dbTicket.isPresent()) return null;
        tickets.remove(dbTicket.get());
        ticket.setId(id);
        tickets.add(ticket);
        return ticket;
    }

    public Boolean delete(int id) {
        Optional<Ticket> post = getOne(id);
        if (!post.isPresent()) return false;
        return tickets.remove(post.get());
    }

}
