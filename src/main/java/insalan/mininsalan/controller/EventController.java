package insalan.mininsalan.controller;

import insalan.mininsalan.dto.EventDateDto;
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

    @GetMapping("/dates")
    public ResponseEntity<List<EventDateDto>> getEventsDates() {
        return ResponseEntity.ok(eventService.getEventsDates());
    }

    @PostMapping
    public ResponseEntity<EventDateDto> createEvent(@RequestBody EventDateDto dto) {
        EventDateDto createdEvent = eventService.createEvent(dto);
        return ResponseEntity.ok(createdEvent);
    }
}
