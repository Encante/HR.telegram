package net.ddns.encante.telegram.HR.Quiz;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
//class for data to form inline keyboards to send quiz questions that bot can send according to harmonogram
public class Quiz {
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
    private static final Logger log = LoggerFactory.getLogger(Quiz.class);
//    for resolving answer only, wont be saved to db
    AnswerCallbackQuery reactionForAnswerCallback;
    SendMessage reactionForAnswerMessage;

    public Quiz (String question, String optA, String optB, String optC, String optD, String correctAnswer){
        this.question = question;
        this.optA = optA;
        this.optB = optB;
        this.optC = optC;
        this.optD = optD;
        this.correctAnswer = correctAnswer;
    }

}