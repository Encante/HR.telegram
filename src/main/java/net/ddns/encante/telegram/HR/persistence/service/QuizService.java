package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.Quiz.Quiz;

public interface QuizService {
    Quiz saveQuiz(Quiz quiz);
    boolean deleteQuizById(final Long quizId);
}
