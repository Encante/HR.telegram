package net.ddns.encante.telegram.HR.Quiz;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
//class for data to form inline keyboards to send quiz questions that bot can send according to harmonogram
public class Quiz {
    Long quizId;
    String question;
    String word;
    String optA;
    String optB;
    String optC;
    String optD;
    String correctAnswer;
    String answer;
    boolean success;
    Long dateSent;
    Long dateAnswered;
}