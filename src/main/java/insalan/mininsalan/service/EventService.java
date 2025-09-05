package insalan.mininsalan.service;

import insalan.mininsalan.dto.EventDateDto;
import insalan.mininsalan.dto.EventDetailsDto;
import insalan.mininsalan.dto.EventSummaryDto;
import insalan.mininsalan.entity.Event;
import insalan.mininsalan.exception.ResourceNotFoundException;
import insalan.mininsalan.mapper.DtoMapper;
import insalan.mininsalan.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final DtoMapper dtoMapper;
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    // PUBLIC METHODS - No user context needed

    /**
     * Get basic event dates for calendars and timelines
     * Cached for performance since this is frequently accessed
     */
    @Cacheable("events-dates")
    @Transactional(readOnly = true)
    public List<EventDateDto> getEventsDates() {
        logger.info("Fetching events dates for public display");
        try {
            List<Event> events = eventRepository.findAllOrderByStartDate();
            return events.stream()
                    .map(this::toEventDateDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching events dates: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get event summaries with challenge counts for listing pages
     * Shows how many challenges each event has and if it's currently active
     */
    @Cacheable("events-summary")
    @Transactional(readOnly = true)
    public List<EventSummaryDto> getEventsSummary() {
        logger.info("Fetching events summary for public display");
        try {
            List<Event> events = eventRepository.findAllWithChallengeCount();
            return events.stream()
                    .map(dtoMapper::toEventSummaryDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching events summary: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get complete event details with all challenges
     * No player context since there's no user system
     */
    @Cacheable(value = "event-details", key = "#eventId")
    @Transactional(readOnly = true)
    public EventDetailsDto getEventDetails(Long eventId) {
        logger.info("Fetching event details for eventId: {}", eventId);
        try {
            Event event = eventRepository.findByIdWithChallenges(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

            // No playerId since there's no user system
            return dtoMapper.toEventDetailsDto(event, null);
        } catch (Exception e) {
            logger.error("Error fetching event details for eventId {}: {}", eventId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get only currently running events for homepage/dashboard
     * Shows events that are happening right now
     */
    @Cacheable("active-events")
    @Transactional(readOnly = true)
    public List<EventSummaryDto> getActiveEvents() {
        logger.info("Fetching currently active events");
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Event> activeEvents = eventRepository.findActiveEvents(now);
            return activeEvents.stream()
                    .map(dtoMapper::toEventSummaryDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching active events: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get upcoming events for announcements and "coming soon" sections
     * Shows events that haven't started yet
     */
    @Cacheable("upcoming-events")
    @Transactional(readOnly = true)
    public List<EventSummaryDto> getUpcomingEvents() {
        logger.info("Fetching upcoming events");
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Event> upcomingEvents = eventRepository.findUpcomingEvents(now);
            return upcomingEvents.stream()
                    .map(dtoMapper::toEventSummaryDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching upcoming events: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ADMIN METHODS - Will require admin auth later

    /**
     * Create a new event - ADMIN ONLY
     * Validates input and creates a new event in the system
     */
    @CacheEvict(value = {"events-dates", "events-summary", "event-details", "active-events", "upcoming-events"}, allEntries = true)
    public EventDateDto createEvent(EventDateDto dto) {
        logger.info("Creating new event: {}", dto.getName());
        try {
            validateEventDto(dto);

            Event event = Event.builder()
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .duration(dto.getDuration())
                    .build();

            Event savedEvent = eventRepository.save(event);
            logger.info("Created event with ID: {}", savedEvent.getId());

            return toEventDateDto(savedEvent);
        } catch (Exception e) {
            logger.error("Error creating event: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update existing event - ADMIN ONLY
     * Modifies event details while preserving challenges
     */
    @CacheEvict(value = {"events-dates", "events-summary", "event-details", "active-events", "upcoming-events"}, allEntries = true)
    public EventDateDto updateEvent(Long eventId, EventDateDto dto) {
        logger.info("Updating event with ID: {}", eventId);
        try {
            Event existingEvent = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

            validateEventDto(dto);

            existingEvent.setName(dto.getName());
            existingEvent.setDescription(dto.getDescription());
            existingEvent.setStartDate(dto.getStartDate());
            existingEvent.setEndDate(dto.getEndDate());
            existingEvent.setDuration(dto.getDuration());

            Event savedEvent = eventRepository.save(existingEvent);
            logger.info("Updated event with ID: {}", savedEvent.getId());

            return toEventDateDto(savedEvent);
        } catch (Exception e) {
            logger.error("Error updating event {}: {}", eventId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete event and all associated data - ADMIN ONLY
     * WARNING: This will also delete all challenges and completions for this event
     */
    @CacheEvict(value = {"events-dates", "events-summary", "event-details", "active-events", "upcoming-events"}, allEntries = true)
    public void deleteEvent(Long eventId) {
        logger.info("Deleting event with ID: {}", eventId);
        try {
            if (!eventRepository.existsById(eventId)) {
                throw new ResourceNotFoundException("Event not found with id: " + eventId);
            }

            // This will cascade delete all challenges and their completions
            eventRepository.deleteById(eventId);
            logger.info("Deleted event with ID: {} and all associated data", eventId);
        } catch (Exception e) {
            logger.error("Error deleting event {}: {}", eventId, e.getMessage(), e);
            throw e;
        }
    }
// HELPER METHODS

    private EventDateDto toEventDateDto(Event event) {
        return EventDateDto.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .duration(event.getDuration())
                .build();
    }

    private void validateEventDto(EventDateDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be null or empty");
        }
        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (dto.getEndDate() == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        if (dto.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
    }
}