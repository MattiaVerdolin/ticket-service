package ch.supsi.webapp.tickets.repository;

import ch.supsi.webapp.tickets.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
}
