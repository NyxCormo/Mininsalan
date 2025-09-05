package insalan.mininsalan.controller;

import insalan.mininsalan.dto.EventDateDto;
import insalan.mininsalan.dto.EventDetailsDto;
import insalan.mininsalan.dto.EventSummaryDto;
import insalan.mininsalan.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // PUBLIC ENDPOINTS - Anyone can access these

    /**
     * Get all events with basic date information
     * Used for: Event calendars, date pickers, timeline views
     */
    @GetMapping("/dates")
    public ResponseEntity<List<EventDateDto>> getEventsDates() {
        return ResponseEntity.ok(eventService.getEventsDates());
    }

    /**
     * Get all events with summary information (includes challenge counts)
     * Used for: Event listing pages, dashboard overviews
     */
    @GetMapping("/summary")
    public ResponseEntity<List<EventSummaryDto>> getEventsSummary() {
        return ResponseEntity.ok(eventService.getEventsSummary());
    }

    /**
     * Get detailed event information with all challenges
     * Used for: Event detail pages, challenge listings
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailsDto> getEventDetails(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventDetails(eventId));
    }

    /**
     * Get only currently active events
     * Used for: Homepage, "current events" sections
     */
    @GetMapping("/active")
    public ResponseEntity<List<EventSummaryDto>> getActiveEvents() {
        return ResponseEntity.ok(eventService.getActiveEvents());
    }

    /**
     * Get upcoming events (future events)
     * Used for: "Coming soon" sections, event announcements
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventSummaryDto>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    // ADMIN ENDPOINTS - Will require admin authentication later

    /**
     * Create a new event
     * Used for: Admin event creation
     */
    @PostMapping
    public ResponseEntity<EventDateDto> createEvent(@RequestBody EventDateDto dto) {
        EventDateDto createdEvent = eventService.createEvent(dto);
        return ResponseEntity.ok(createdEvent);
    }

    /**
     * Update an existing event
     * Used for: Admin event modification
     */
    @PutMapping("/{eventId}")
    public ResponseEntity<EventDateDto> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventDateDto dto) {
        EventDateDto updatedEvent = eventService.updateEvent(eventId, dto);
        return ResponseEntity.ok(updatedEvent);
    }

    /**
     * Delete an event and all its challenges
     * Used for: Admin event removal
     */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}