package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;

public interface QuizService {
    Quiz saveQuiz(Quiz quiz);
    Quiz getFirstQuizFromDb();
    Quiz getNextQuizToSendFromDb();
    Quiz getQuizByMessageId(final Long messageId);
    boolean deleteQuizById(final Long quizId);
    SendMessage createQuizMessage(Long chatId, Quiz quiz);
    void sendQuizToId (Long chatId);
    void resolveQuizAnswer(WebhookUpdate update);
    void sendQuizToYasia();
}
