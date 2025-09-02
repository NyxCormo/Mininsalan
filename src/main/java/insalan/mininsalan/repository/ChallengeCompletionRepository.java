package insalan.mininsalan.repository;

import insalan.mininsalan.entity.ChallengeCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeCompletionRepository extends JpaRepository<ChallengeCompletion, Long> {
    List<ChallengeCompletion> findByPlayerId(Long playerId);
    List<ChallengeCompletion> findByChallengeId(Long challengeId);
}
