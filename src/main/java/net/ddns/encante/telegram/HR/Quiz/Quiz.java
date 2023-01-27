package net.ddns.encante.telegram.HR.Quiz;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;

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

    public Quiz (String question, String optA, String optB, String optC, String optD, String correctAnswer){
        this.setQuestion(question);
        this.setOptA(optA);
        this.setOptB(optB);
        this.setOptC(optC);
        this.setOptD(optD);
        this.setCorrectAnswer(correctAnswer);
    }
    public SendMessage createQuizMessageFromCommand (WebhookUpdate update){
        String[] commands = update.getMessage().getText().split(" ");
            String[] keys = {commands[2],commands[3],commands[4],commands[5]};
            return new SendMessage()
                    .setText(commands[1])
                    .setReply_markup(new InlineKeyboardMarkup
                            .KeyboardBuilder(1,4,keys).build())
                    .setChat_id(update.getMessage().getChat().getId());
    }
}