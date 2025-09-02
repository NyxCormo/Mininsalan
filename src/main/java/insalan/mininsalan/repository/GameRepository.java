package insalan.mininsalan.repository;

import insalan.mininsalan.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
