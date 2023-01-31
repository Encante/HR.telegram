package net.ddns.encante.telegram.HR.persistence.repository;

import net.ddns.encante.telegram.HR.persistence.entities.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
//    @Query("SELECT q FROM QuizEntity q ")
    QuizEntity findFirstByOrderByKeyIdAsc();
    @Query("SELECT q FROM QuizEntity q WHERE q.success IS NULL OR q.success IS NULL")
    List<QuizEntity> findAllNotSentQuiz();
    QuizEntity findByMessageId(Long messageId);
}