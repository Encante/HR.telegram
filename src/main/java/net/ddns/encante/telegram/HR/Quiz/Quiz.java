package net.ddns.encante.telegram.HR.Quiz;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.Chat;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;
import net.ddns.encante.telegram.HR.Utils;

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
    Boolean success;
    Long dateSent;
    Long dateAnswered;
    Long messageId;

    public Quiz (String question, String optA, String optB, String optC, String optD, String correctAnswer){
        this.question = question;
        this.optA = optA;
        this.optB = optB;
        this.optC = optC;
        this.optD = optD;
        this.correctAnswer = correctAnswer;
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

    public SendMessage createMessage (Chat chat){
        String[] keys = {this.optA,this.optB,this.optC,this.optD};
        return new SendMessage()
                .setText(this.question)
                .setReply_markup(new InlineKeyboardMarkup
                        .KeyboardBuilder(1,4,keys).build())
                .setChat_id(chat.getId());
    }
    public Quiz resolveAnswer (WebhookUpdate update){
        this.dateAnswered = Utils.getCurrentUnixTime();
        this.answer = update.getCallback_query().getData();
        if (this.answer.equals(this.correctAnswer))
                this.success = true;
        else {
            this.success = false;
        }
        return this;
    }
}