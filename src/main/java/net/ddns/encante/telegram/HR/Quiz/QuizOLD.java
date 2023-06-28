package net.ddns.encante.telegram.HR.Quiz;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;

@Getter
@Setter
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
//class for data to form inline keyboards to send quiz questions that bot can send according to harmonogram
public class QuizOLD {
    Long quizId;
    String question;
    Integer retriesCount = 0;
    Integer answersLeft = 4;
    String optA;
    String optB;
    String optC;
    String optD;
    String correctAnswer;
    String lastAnswer;
    Boolean success;
    Long dateSent;
    Long dateAnswered;
    Long messageId;
//    for resolving answer only, wont be saved to db
    AnswerCallbackQuery reactionForAnswerCallback;
    SendMessage reactionForAnswerMessage;


}