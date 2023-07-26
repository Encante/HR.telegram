package net.ddns.encante.telegram.HR.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.date BETWEEN ?1 - 604000L AND ?1")
    List<Event> findAllEventsFromLastWeek(Long currentDate);
    @Query("SELECT e FROM Event e WHERE e.date BETWEEN ?1 - 604000L AND ?1 AND (e.type = net.ddns.encante.telegram.HR.events.EventType.QUIZ_BAD_ANSWER OR e.type = net.ddns.encante.telegram.HR.events.EventType.QUIZ_FR_BAD_ANSWER)")
    List<Event> findBadAnswerEventsFromLastWeek(Long currentDate);
    @Query("SELECT e FROM Event e WHERE e.date BETWEEN ?1 - 604000L AND ?1 AND (e.type = net.ddns.encante.telegram.HR.events.EventType.QUIZ_GOOD_ANSWER OR e.type = net.ddns.encante.telegram.HR.events.EventType.QUIZ_FR_GOOD_ANSWER)")
    List<Event> findGoodAnswerEventsFromLastWeek(Long currentDate);
}
