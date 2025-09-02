package insalan.mininsalan.repository;

import insalan.mininsalan.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
