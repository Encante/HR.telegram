package net.ddns.encante.telegram.hr.telegram.service;

import lombok.Getter;
import lombok.Setter;
import net.ddns.encante.telegram.hr.Utils;
import net.ddns.encante.telegram.hr.quiz.entity.Quiz;
import net.ddns.encante.telegram.hr.request.UnirestRequest;
import net.ddns.encante.telegram.hr.telegram.api.methods.*;
import net.ddns.encante.telegram.hr.telegram.api.objects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.hr.telegram.api.objects.SentMessage;
import net.ddns.encante.telegram.hr.telegram.entity.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Getter
@Setter
@Service
public class MessageManager {
    private UnirestRequest request;
    private static final Logger log = LoggerFactory.getLogger(MessageManager.class);
    User originalSender;
    private final Long ME = 5580797031L;
    private final Long YASIA = 566760042L;
    private final Long CHOMIK = 6182762959L;
    public MessageManager(UnirestRequest request){
        this.request = request;
    }
    public SentMessage sendTelegramObjAsMessage(SendMessage message){
        return request.sendTelegramMessageObj(message);
    }
    public SentMessage sendTelegramTextMessage(String text, Long chatId) {
        return request.sendTelegramMessageObj(new SendMessage()
                .setText(text)
                .setChat_id(chatId));
    }
    public SentMessage editTelegramTextMessage (EditMessageText msg){
        log.info("editTelegramTextMessage Fired.");
        return request.editTelegramMessageText(msg);
    }
    public SentMessage editTelegramMessageText(Long chatId, Long messageId, String text){
        return request.editTelegramMessageText(EditMessageText.builder().chat_id(chatId).message_id(messageId).text(text).build());
    }
    public SentMessage editTelegramMessageReplyMarkup (Long chatId,Long messageId, InlineKeyboardMarkup replyMarkup){
        return request.editTelegramMessageReplyMarkup(new EditMessageReplyMarkup(chatId,messageId, replyMarkup));
    }
    public boolean deleteTelegramMessage(Long chatId, Long messageId){
        return request.deleteTelegramMessage(new DeleteMessage(chatId,messageId));
    }
    public void answerCallbackQuery(AnswerCallbackQuery answer){
        request.answerCallbackQuery(answer);
    }

    public void sendAndLogErrorMsg(@NotNull String error) {
            log.warn(error);
            sendTelegramTextMessage(error, ME);
    }

    public SentMessage sendBackTelegramTextMessage(String text) {
        if (originalSender != null) {
            return request.sendTelegramMessageObj(new SendMessage()
                    .setText(text)
                    .setChat_id(originalSender.getId()));
        } else {
            String err = "ERROR - backToSender is null. Method: sendBackTelegramTextMessage. Text: " + text;
            sendAndLogErrorMsg(err);
            throw new RuntimeException(err);
        }
    }

    public void sendQuizResultInfo(Quiz quiz, Long whoTo) {
        if (quiz.getSuccess() == true) {
            sendTelegramTextMessage("Dobra odpowiedź na pytanie: " + quiz.getQuestion(), whoTo);
        } else if (quiz.getSuccess() == false) {
            sendTelegramTextMessage("Zła odpowiedź na pytanie: " + quiz.getQuestion() + " Odpowiedź: " + quiz.getLastAnswer(), whoTo);
        }
    }
    public SentMessage greet() {
        if (originalSender != null) {
            if (originalSender.getLast_name() != null) {
                return sendBackTelegramTextMessage("Hey " + originalSender.getFirst_name() + " " + originalSender.getLast_name() + "! Have a nice day =]");
            } else {
                return sendBackTelegramTextMessage("Hello " + originalSender.getFirst_name() + "! Have a nice day =]");
            }
        } else {
            String error = "ERROR - backToSender is null. Method: greet.";
            sendAndLogErrorMsg(error);
            throw new RuntimeException(error);
        }
    }

    public SentMessage greetFirstTime() {
        if (originalSender != null) {
            if (originalSender.getLast_name() != null) {
                return sendBackTelegramTextMessage("Hello " + originalSender.getFirst_name() + " " + originalSender.getLast_name() + "! Nice to see you! Hope You'll have a good time =]");
            } else {
                return sendBackTelegramTextMessage("Hello " + originalSender.getFirst_name() + "! Nice to see you! Hope You'll have a good time =]");
            }
        } else {
            String err = "ERROR! backToSender is null. Invoker: greetFirstTime";
            sendAndLogErrorMsg(err);
            throw new RuntimeException(err);
        }
    }

    @PreDestroy
    private void messageOnQuit(){
        sendTelegramTextMessage("To już jest koniec \n T: "+ Utils.getCurrentDateTime(),getME());
        sendTelegramTextMessage("Chwilowa przerwa w działaniu bota. Wrócę za chwilę ;) \n Bot jest wyłączony.", getYASIA());
    }
}
