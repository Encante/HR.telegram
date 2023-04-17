package net.ddns.encante.telegram.HR.TelegramMethods;

import lombok.Data;
import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.RemoteRequest.UnirestRequest;
import net.ddns.encante.telegram.HR.TelegramObjects.SentMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Data
public class MessageSender {
    @Autowired
    UnirestRequest request;
    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);
    private User backToSender;
    private final Long ME = 5580797031L;
    private final Long YASIA = 566760042L;
    private final Long CHOMIK = 6182762959L;
    private String error;

    public SentMessage sendTelegramTextMessage (String text, Long chatId){
        return request.sendTelegramMessage(new SendMessage()
                .setText(text)
                .setChat_id(chatId));
    }
    public void sendAndLogErrorMsg(){
        if (this.error != null){
            log.warn(this.error);
            sendTelegramTextMessage(this.error, ME);
        }else {
            log.warn("Error is null");
            throw new RuntimeException("Error is null");
        }
    }
    public SentMessage sendBackTelegramTextMessage (String text){
        if (this.backToSender!=null) {
            return request.sendTelegramMessage(new SendMessage()
                    .setText(text)
                    .setChat_id(backToSender.getId()));
        }else {
            this.error = "ERROR - backToSender is null. Method: sendBackTelegramTextMessage. Text: "+text;
            sendAndLogErrorMsg();
            throw new RuntimeException(error);
        }
    }
    public void sendQuizResultInfo(Quiz quiz, Long whoTo){
        if (quiz.getSuccess() == true){
            sendTelegramTextMessage("Dobra odpowiedź na pytanie: " + quiz.getQuestion(), whoTo);
        } else if (quiz.getSuccess() == false) {
            sendTelegramTextMessage("Zła odpowiedź na pytanie: " + quiz.getQuestion() + " Odpowiedź: " + quiz.getLastAnswer(),whoTo);
        }
    }
    public SentMessage greet (){
        if (backToSender!= null) {
            if (backToSender.getLast_name() != null) {
                return sendBackTelegramTextMessage("Hey " + backToSender.getFirst_name() + " " + backToSender.getLast_name() + "! Have a nice day =]");
            } else {
                return sendBackTelegramTextMessage("Hello " + backToSender.getFirst_name() + "! Have a nice day =]");
            }
        }else {
            this.error = "ERROR - backToSender is null. Method: greet.";
            sendAndLogErrorMsg();
            throw new RuntimeException(error);
        }
    }
    public SentMessage greetFirstTime(){
        if (backToSender!=null) {
            if (backToSender.getLast_name() != null) {
                return sendBackTelegramTextMessage("Hello " + backToSender.getFirst_name() + " " + backToSender.getLast_name() + "! Nice to see you! Hope You'll have a good time =]");
            } else {
                return sendBackTelegramTextMessage("Hello " + backToSender.getFirst_name() + "! Nice to see you! Hope You'll have a good time =]");
            }
        }else {
            log.warn("ERROR! backToSender is null. Invoker: greetFirstTime");
            sendTelegramTextMessage("ERROR! backToSender is null. Invoker: greetFirstTime",ME);
            throw new RuntimeException("ERROR! backToSender is null. Invoker: greetFirstTime");
        }
    }
}
