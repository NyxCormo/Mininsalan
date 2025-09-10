package insalan.mininsalan.service;

import insalan.mininsalan.dto.ChallengeDto;
import insalan.mininsalan.entity.Challenge;
import insalan.mininsalan.entity.Event;
import insalan.mininsalan.entity.Game;
import insalan.mininsalan.entity.Category;
import insalan.mininsalan.enums.ChallengeType;
import insalan.mininsalan.exception.ResourceNotFoundException;
import insalan.mininsalan.repository.ChallengeRepository;
import insalan.mininsalan.repository.EventRepository;
import insalan.mininsalan.repository.GameRepository;
import insalan.mininsalan.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final GameRepository gameRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(ChallengeService.class);

    /**
     * Get all challenges
     * Used for: Challenge management pages
     */
    @Cacheable("challenges")
    @Transactional(readOnly = true)
    public List<ChallengeDto> getAllChallenges() {
        logger.info("Fetching all challenges");
        try {
            List<Challenge> challenges = challengeRepository.findAll();
            return challenges.stream()
                    .map(this::toChallengeDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching challenges: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get challenges by event ID
     * Used for: Event detail pages
     */
    @Cacheable(value = "challenges-by-event", key = "#eventId")
    @Transactional(readOnly = true)
    public List<ChallengeDto> getChallengesByEventId(Long eventId) {
        logger.info("Fetching challenges for eventId: {}", eventId);
        try {
            List<Challenge> challenges = challengeRepository.findByEventId(eventId);
            return challenges.stream()
                    .map(this::toChallengeDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching challenges for eventId {}: {}", eventId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get a single challenge by ID
     * Used for: Challenge details
     */
    @Cacheable(value = "challenge", key = "#challengeId")
    @Transactional(readOnly = true)
    public ChallengeDto getChallengeById(Long challengeId) {
        logger.info("Fetching challenge with ID: {}", challengeId);
        try {
            Challenge challenge = challengeRepository.findById(challengeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));
            return toChallengeDto(challenge);
        } catch (Exception e) {
            logger.error("Error fetching challenge {}: {}", challengeId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Create a new challenge
     * Used for: Admin challenge creation
     */
    @CacheEvict(value = {"challenges", "challenges-by-event", "challenge"}, allEntries = true)
    public ChallengeDto createChallenge(ChallengeDto dto) {
        logger.info("Creating new challenge: {}", dto.getTitle());
        try {
            validateChallengeDto(dto);

            Game game = gameRepository.findById(dto.getGame().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + dto.getGame().getId()));
            Event event = eventRepository.findById(dto.getEvent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + dto.getEvent().getId()));
            Set<Category> categories = categoryRepository.findAllById(dto.getCategories().stream().map(ChallengeDto.CategoryDto::getId).collect(Collectors.toSet()))
                    .stream().collect(Collectors.toSet());

            // Set endTime to event's endDate for PERMANENT and RACE challenges
            LocalDateTime endTime = dto.getType() == ChallengeType.TEMPORARY ? dto.getEndTime() : event.getEndDate();

            Challenge challenge = Challenge.builder()
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .points(dto.getPoints())
                    .type(dto.getType())
                    .reward(dto.getReward())
                    .releaseTime(dto.getReleaseTime())
                    .endTime(endTime)
                    .game(game)
                    .event(event)
                    .categories(categories)
                    .build();

            Challenge savedChallenge = challengeRepository.save(challenge);
            logger.info("Created challenge with ID: {}", savedChallenge.getId());

            return toChallengeDto(savedChallenge);
        } catch (Exception e) {
            logger.error("Error creating challenge: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update an existing challenge
     * Used for: Admin challenge modification
     */
    @CacheEvict(value = {"challenges", "challenges-by-event", "challenge"}, allEntries = true)
    public ChallengeDto updateChallenge(Long challengeId, ChallengeDto dto) {
        logger.info("Updating challenge with ID: {}", challengeId);
        try {
            Challenge existingChallenge = challengeRepository.findById(challengeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

            validateChallengeDto(dto);

            Game game = gameRepository.findById(dto.getGame().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + dto.getGame().getId()));
            Event event = eventRepository.findById(dto.getEvent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + dto.getEvent().getId()));
            Set<Category> categories = categoryRepository.findAllById(dto.getCategories().stream().map(ChallengeDto.CategoryDto::getId).collect(Collectors.toSet()))
                    .stream().collect(Collectors.toSet());

            // Set endTime to event's endDate for PERMANENT and RACE challenges
            LocalDateTime endTime = dto.getType() == ChallengeType.TEMPORARY ? dto.getEndTime() : event.getEndDate();

            existingChallenge.setTitle(dto.getTitle());
            existingChallenge.setDescription(dto.getDescription());
            existingChallenge.setPoints(dto.getPoints());
            existingChallenge.setType(dto.getType());
            existingChallenge.setReward(dto.getReward());
            existingChallenge.setReleaseTime(dto.getReleaseTime());
            existingChallenge.setEndTime(endTime);
            existingChallenge.setGame(game);
            existingChallenge.setEvent(event);
            existingChallenge.setCategories(categories);

            Challenge savedChallenge = challengeRepository.save(existingChallenge);
            logger.info("Updated challenge with ID: {}", savedChallenge.getId());

            return toChallengeDto(savedChallenge);
        } catch (Exception e) {
            logger.error("Error updating challenge {}: {}", challengeId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete a challenge
     * Used for: Admin challenge removal
     */
    @CacheEvict(value = {"challenges", "challenges-by-event", "challenge"}, allEntries = true)
    public void deleteChallenge(Long challengeId) {
        logger.info("Deleting challenge with ID: {}", challengeId);
        try {
            if (!challengeRepository.existsById(challengeId)) {
                throw new ResourceNotFoundException("Challenge not found with id: " + challengeId);
            }

            challengeRepository.deleteById(challengeId);
            logger.info("Deleted challenge with ID: {}", challengeId);
        } catch (Exception e) {
            logger.error("Error deleting challenge {}: {}", challengeId, e.getMessage(), e);
            throw e;
        }
    }

    // Helper method to convert Challenge entity to DTO
    private ChallengeDto toChallengeDto(Challenge challenge) {
        return ChallengeDto.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .points(challenge.getPoints())
                .type(challenge.getType())
                .reward(challenge.getReward())
                .releaseTime(challenge.getReleaseTime())
                .endTime(challenge.getEndTime())
                .game(challenge.getGame() != null ? ChallengeDto.GameDto.builder()
                        .id(challenge.getGame().getId())
                        .name(challenge.getGame().getName())
                        .link(challenge.getGame().getLink())
                        .imagelink(challenge.getGame().getImagelink())
                        .build() : null)
                .event(challenge.getEvent() != null ? ChallengeDto.EventDto.builder()
                        .id(challenge.getEvent().getId())
                        .name(challenge.getEvent().getName())
                        .startDate(challenge.getEvent().getStartDate())
                        .endDate(challenge.getEvent().getEndDate())
                        .build() : null)
                .categories(challenge.getCategories().stream()
                        .map(category -> ChallengeDto.CategoryDto.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    // Validation logic for ChallengeDto
    private void validateChallengeDto(ChallengeDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Challenge title cannot be null or empty");
        }
        if (dto.getPoints() < 0) {
            throw new IllegalArgumentException("Points must be non-negative");
        }
        if (dto.getType() == null) {
            throw new IllegalArgumentException("Challenge type cannot be null");
        }
        if (dto.getReward() == null) {
            throw new IllegalArgumentException("Reward cannot be null");
        }
        if (dto.getReleaseTime() == null) {
            throw new IllegalArgumentException("Release time cannot be null");
        }
        if (dto.getType() == ChallengeType.TEMPORARY && dto.getEndTime() == null) {
            throw new IllegalArgumentException("End time cannot be null for TEMPORARY challenges");
        }
        if (dto.getType() == ChallengeType.TEMPORARY && dto.getReleaseTime() != null && dto.getEndTime() != null && dto.getReleaseTime().isAfter(dto.getEndTime())) {
            throw new IllegalArgumentException("Release time cannot be after end time for TEMPORARY challenges");
        }
        if (dto.getGame() == null || dto.getGame().getId() == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        if (dto.getEvent() == null || dto.getEvent().getId() == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
    }
}