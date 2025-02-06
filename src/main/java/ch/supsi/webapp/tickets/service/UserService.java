package ch.supsi.webapp.tickets.service;

import ch.supsi.webapp.tickets.model.User;
import ch.supsi.webapp.tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
