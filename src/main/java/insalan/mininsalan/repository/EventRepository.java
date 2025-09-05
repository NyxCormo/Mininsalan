package insalan.mininsalan.repository;

import insalan.mininsalan.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Get all events ordered by start date for chronological display
     */
    @Query("SELECT e FROM Event e ORDER BY e.startDate")
    List<Event> findAllOrderByStartDate();

    /**
     * Get event with all its challenges loaded for detailed view
     */
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.challenges WHERE e.id = :eventId")
    Optional<Event> findByIdWithChallenges(@Param("eventId") Long eventId);

    /**
     * Get all events with challenge count for summary displays
     */
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.challenges")
    List<Event> findAllWithChallengeCount();

    /**
     * Get events that are currently running (between start and end date)
     */
    @Query("SELECT e FROM Event e WHERE e.startDate <= :now AND e.endDate >= :now ORDER BY e.startDate")
    List<Event> findActiveEvents(@Param("now") LocalDateTime now);

    /**
     * Get events that haven't started yet
     */
    @Query("SELECT e FROM Event e WHERE e.startDate > :now ORDER BY e.startDate")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);

    /**
     * Get events that have ended
     */
    @Query("SELECT e FROM Event e WHERE e.endDate < :now ORDER BY e.endDate DESC")
    List<Event> findPastEvents(@Param("now") LocalDateTime now);
}