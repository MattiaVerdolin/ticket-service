package ch.supsi.webapp.tickets.service;

import ch.supsi.webapp.tickets.model.Tag;
import ch.supsi.webapp.tickets.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Recupera tutti i tag disponibili nel database.
     * @return Una lista di nomi dei tag.
     */
    public List<String> getAllTags() {
        return tagRepository.findAll().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    /**
     * Aggiunge un nuovo tag se non esiste già.
     * @param tagName Nome del tag da aggiungere.
     * @return Il tag appena creato o già esistente.
     */
    public Tag addTag(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseGet(() -> tagRepository.save(new Tag(tagName)));
    }
}
