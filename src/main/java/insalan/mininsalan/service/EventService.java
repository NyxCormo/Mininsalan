package insalan.mininsalan.service;

import insalan.mininsalan.dto.EventDateDto;
import insalan.mininsalan.entity.Event;
import insalan.mininsalan.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    @Cacheable("events")
    @Transactional(readOnly = true)
    public List<EventDateDto> getEventsDates() {
        List<Event> events = eventRepository.findAll();
        List<EventDateDto> eventDateDtos = new ArrayList<>();
        for (Event event : events) {
            eventDateDtos.add(EventDateDto.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .duration(event.getDuration())
                    .startDate(event.getStartDate())
                    .endDate(event.getEndDate())
                    .build());
        }
        return eventDateDtos;
    }

    @CacheEvict(value = "events", allEntries = true)
    public EventDateDto createEvent(EventDateDto dto) {
        dto.setId(eventRepository.save(Event.builder()
                .name(dto.getName())
                .duration(dto.getDuration())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build()).getId());
        return dto;
    }
}
