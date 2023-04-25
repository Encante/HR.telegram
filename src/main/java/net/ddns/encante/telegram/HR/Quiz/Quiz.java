package net.ddns.encante.telegram.HR.Quiz;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;
import net.ddns.encante.telegram.HR.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


//    checking answer based on webhook update sent by telegram
    public Quiz resolveAnswer (WebhookUpdate update){
//        saving metadata
        this.dateAnswered = Utils.getCurrentUnixTime();
        this.lastAnswer = update.getCallback_query().getData();

//        actual check
        if (this.lastAnswer.equals(this.correctAnswer)){
                this.success = true;
//                react for answer
                this.reactionForAnswerCallback = new AnswerCallbackQuery(update.getCallback_query().getId(),"Bardzo dobrze!",true);
                this.reactionForAnswerMessage = new SendMessage()
                        .setText("Dobra odpowiedź! ;)")
                        .setReply_to_message_id(update.getCallback_query().getMessage().getMessage_id())
                        .setChat_id(update.getCallback_query().getMessage().getChat().getId());
        }
        else {
            this.success = false;
            this.answersLeft--;
            this.retriesCount++;
            this.reactionForAnswerCallback = new AnswerCallbackQuery(update.getCallback_query().getId(),"Zła odpowiedź!",true);

//            check if it isn't last answer
            if (this.answersLeft>1) {
//                randomize answers and set used on last places
                List<String> answers = new ArrayList<>(List.of(this.optA, this.optB, this.optC, this.optD));
                List<String> usedAnswers = new ArrayList<>();
                    switch (this.answersLeft) {
                        case 3 -> {
                            usedAnswers.add(this.lastAnswer);
                            answers.remove(this.lastAnswer);
                            Collections.shuffle(answers);
                            answers.add(this.lastAnswer);
                        }
                        case 2 -> {
                            usedAnswers.add(this.optD);
                            usedAnswers.add(this.lastAnswer);
                            answers.removeAll(usedAnswers);
                            Collections.shuffle(answers);
                            answers.add(usedAnswers.get(0));
                            answers.add(usedAnswers.get(1));
                        }
                    }
                this.optA = answers.get(0);
                this.optB = answers.get(1);
                this.optC = answers.get(2);
                this.optD = answers.get(3);
//
//                react for answer
                this.reactionForAnswerMessage = new SendMessage()
                        .setText("Niestety zła odpowiedź :(")
                        .setReply_to_message_id(update.getCallback_query().getMessage().getMessage_id())
                        .setChat_id(update.getCallback_query().getMessage().getChat().getId());
            }
            else {
                this.reactionForAnswerMessage = new SendMessage()
                        .setText("Niestety zła odpowiedź :( Prawidłowa odpowiedź to: "+ this.correctAnswer)
                        .setReply_to_message_id(update.getCallback_query().getMessage().getMessage_id())
                        .setChat_id(update.getCallback_query().getMessage().getChat().getId());
            }
        }
        return this;
    }
}