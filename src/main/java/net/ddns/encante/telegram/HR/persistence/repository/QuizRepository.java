package net.ddns.encante.telegram.HR.persistence.repository;

import net.ddns.encante.telegram.HR.persistence.entities.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, Long> {

}
