package insalan.mininsalan.service;

import insalan.mininsalan.dto.GameDto;
import insalan.mininsalan.entity.Game;
import insalan.mininsalan.exception.ResourceNotFoundException;
import insalan.mininsalan.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    /**
     * Get all games
     * Used for: Game management pages, dropdowns
     */
    @Cacheable("games")
    @Transactional(readOnly = true)
    public List<GameDto> getAllGames() {
        logger.info("Fetching all games");
        try {
            List<Game> games = gameRepository.findAll();
            return games.stream()
                    .map(this::toGameDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching games: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get a single game by ID
     * Used for: Game details
     */
    @Cacheable(value = "game", key = "#gameId")
    @Transactional(readOnly = true)
    public GameDto getGameById(Long gameId) {
        logger.info("Fetching game with ID: {}", gameId);
        try {
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
            return toGameDto(game);
        } catch (Exception e) {
            logger.error("Error fetching game {}: {}", gameId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Create a new game
     * Used for: Admin game creation
     */
    @CacheEvict(value = {"games", "game"}, allEntries = true)
    public GameDto createGame(GameDto dto) {
        logger.info("Creating new game: {}", dto.getName());
        try {
            validateGameDto(dto);

            Game game = Game.builder()
                    .name(dto.getName())
                    .link(dto.getLink())
                    .imagelink(dto.getImagelink())
                    .build();

            Game savedGame = gameRepository.save(game);
            logger.info("Created game with ID: {}", savedGame.getId());

            return toGameDto(savedGame);
        } catch (Exception e) {
            logger.error("Error creating game: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update an existing game
     * Used for: Admin game modification
     */
    @CacheEvict(value = {"games", "game"}, allEntries = true)
    public GameDto updateGame(Long gameId, GameDto dto) {
        logger.info("Updating game with ID: {}", gameId);
        try {
            Game existingGame = gameRepository.findById(gameId)
                    .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

            validateGameDto(dto);

            existingGame.setName(dto.getName());
            existingGame.setLink(dto.getLink());
            existingGame.setImagelink(dto.getImagelink());

            Game savedGame = gameRepository.save(existingGame);
            logger.info("Updated game with ID: {}", savedGame.getId());

            return toGameDto(savedGame);
        } catch (Exception e) {
            logger.error("Error updating game {}: {}", gameId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete a game
     * Used for: Admin game removal
     */
    @CacheEvict(value = {"games", "game"}, allEntries = true)
    public void deleteGame(Long gameId) {
        logger.info("Deleting game with ID: {}", gameId);
        try {
            if (!gameRepository.existsById(gameId)) {
                throw new ResourceNotFoundException("Game not found with id: " + gameId);
            }

            gameRepository.deleteById(gameId);
            logger.info("Deleted game with ID: {}", gameId);
        } catch (Exception e) {
            logger.error("Error deleting game {}: {}", gameId, e.getMessage(), e);
            throw e;
        }
    }

    // Helper method to convert Game entity to DTO
    private GameDto toGameDto(Game game) {
        return GameDto.builder()
                .id(game.getId())
                .name(game.getName())
                .link(game.getLink())
                .imagelink(game.getImagelink())
                .build();
    }

    // Validation logic for GameDto
    private void validateGameDto(GameDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be null or empty");
        }
        if (dto.getLink() == null || dto.getLink().trim().isEmpty()) {
            throw new IllegalArgumentException("Game link cannot be null or empty");
        }
    }
}