package insalan.mininsalan.repository;

import insalan.mininsalan.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByEventId(Long eventId);
}
