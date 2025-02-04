package ch.supsi.webapp.tickets.service;

import ch.supsi.webapp.tickets.model.Milestone;
import ch.supsi.webapp.tickets.model.MilestoneStatus;
import ch.supsi.webapp.tickets.model.Ticket;
import ch.supsi.webapp.tickets.model.TicketStatus;
import ch.supsi.webapp.tickets.repository.MilestoneRepository;
import ch.supsi.webapp.tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MilestoneService {
    @Autowired
    private final MilestoneRepository milestoneRepository;

    public MilestoneService(MilestoneRepository milestoneRepository) {
        this.milestoneRepository = milestoneRepository;
    }

    public List<Milestone> getAll() {
        return milestoneRepository.findAll();
    }

    public Milestone post(Milestone milestone) {
        milestone.setStatus(MilestoneStatus.IN_PROGRESS);
        milestone.setDate(new Date());
        return milestoneRepository.save(milestone);
    }

    public Milestone get(Long id) {
        return milestoneRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public Milestone getMilestoneById(Long id) {
        return milestoneRepository.findById(id).orElse(null);
    }

    public void save(Milestone milestone) {
        milestoneRepository.save(milestone);
    }


}
