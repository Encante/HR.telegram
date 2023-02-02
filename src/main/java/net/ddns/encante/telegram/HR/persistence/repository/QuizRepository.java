package net.ddns.encante.telegram.HR.persistence.repository;

import net.ddns.encante.telegram.HR.persistence.entities.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
    QuizEntity findFirstByOrderByKeyIdAsc();
//    returns list of all quiz entities that were either not yet send or has been sent but remain answered badly but not the ones that are waiting in chat to be answered (q.answer is null for this cases)
    @Query("SELECT q FROM QuizEntity q WHERE q.success IS NULL OR (q.success = 0 AND q.answer IS NOT NULL)")
    List<QuizEntity> findAllQuizEntitiesToSend();
//    returns list of all quiz entities that are waiting in chat to be answered
    @Query("SELECT q From QuizEntity q WHERE q.success = 0 AND q.answer IS NULL")
    List<QuizEntity> findAllSentButNotAnsweredQuizEntities();
    QuizEntity findByMessageId(Long messageId);
}