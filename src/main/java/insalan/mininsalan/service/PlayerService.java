package insalan.mininsalan.service;

import insalan.mininsalan.entity.Player;
import insalan.mininsalan.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public Player createPlayer(String username) {
        return playerRepository.findByUsername(username)
                .orElseGet(() -> playerRepository.save(Player.builder().username(username).build()));
    }

    public Optional<Player> getPlayer(Long id) {
        return playerRepository.findById(id);
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
}
