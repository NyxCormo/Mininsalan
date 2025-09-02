package insalan.mininsalan.service;

import insalan.mininsalan.entity.Event;
import insalan.mininsalan.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEvent(Long id) {
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
}
