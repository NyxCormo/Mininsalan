package insalan.mininsalan.controller;

import insalan.mininsalan.dto.GameDto;
import insalan.mininsalan.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * Get all games
     * Used for: Game management pages, dropdowns
     */
    @GetMapping
    public ResponseEntity<List<GameDto>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllGames());
    }

    /**
     * Get a single game by ID
     * Used for: Game details
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<GameDto> getGameById(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGameById(gameId));
    }

    /**
     * Create a new game
     * Used for: Admin game creation
     */
    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameDto dto) {
        return ResponseEntity.ok(gameService.createGame(dto));
    }

    /**
     * Update an existing game
     * Used for: Admin game modification
     */
    @PutMapping("/{gameId}")
    public ResponseEntity<GameDto> updateGame(
            @PathVariable Long gameId,
            @RequestBody GameDto dto) {
        return ResponseEntity.ok(gameService.updateGame(gameId, dto));
    }

    /**
     * Delete a game
     * Used for: Admin game removal
     */
    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }
}