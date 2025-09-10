package insalan.mininsalan.controller;

import insalan.mininsalan.dto.ChallengeDto;
import insalan.mininsalan.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    /**
     * Get all challenges
     * Used for: Challenge management pages
     */
    @GetMapping
    public ResponseEntity<List<ChallengeDto>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    /**
     * Get a single challenge by ID
     * Used for: Challenge details
     */
    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeDto> getChallengeById(@PathVariable Long challengeId) {
        return ResponseEntity.ok(challengeService.getChallengeById(challengeId));
    }

    /**
     * Get challenges by event ID
     * Used for: Event detail pages
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ChallengeDto>> getChallengesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(challengeService.getChallengesByEventId(eventId));
    }

    /**
     * Create a new challenge
     * Used for: Admin challenge creation
     */
    @PostMapping
    public ResponseEntity<ChallengeDto> createChallenge(@RequestBody ChallengeDto dto) {
        return ResponseEntity.ok(challengeService.createChallenge(dto));
    }

    /**
     * Update an existing challenge
     * Used for: Admin challenge modification
     */
    @PutMapping("/{challengeId}")
    public ResponseEntity<ChallengeDto> updateChallenge(
            @PathVariable Long challengeId,
            @RequestBody ChallengeDto dto) {
        return ResponseEntity.ok(challengeService.updateChallenge(challengeId, dto));
    }

    /**
     * Delete a challenge
     * Used for: Admin challenge removal
     */
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long challengeId) {
        challengeService.deleteChallenge(challengeId);
        return ResponseEntity.noContent().build();
    }
}