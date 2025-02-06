package ch.supsi.webapp.tickets.repository;

import ch.supsi.webapp.tickets.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> { }
