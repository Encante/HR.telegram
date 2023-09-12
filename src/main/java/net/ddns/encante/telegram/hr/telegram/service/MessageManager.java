package net.ddns.encante.telegram.hr.telegram.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.ddns.encante.telegram.hr.Utils;
import net.ddns.encante.telegram.hr.quiz.entity.Quiz;
import net.ddns.encante.telegram.hr.request.UnirestRequest;
import net.ddns.encante.telegram.hr.telegram.api.methods.*;
import net.ddns.encante.telegram.hr.telegram.api.objects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.hr.telegram.api.objects.SentMessage;
import net.ddns.encante.telegram.hr.telegram.api.objects.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
@Slf4j
@Getter
@Setter
@Service
public class MessageManager {
    private UnirestRequest request;
    User originalSender;
    private final Long ME = 5580797031L;
    private final Long YASIA = 566760042L;
    private final Long CHOMIK = 6182762959L;
    public MessageManager(UnirestRequest request){
        this.request = request;
    }
    public SentMessage sendTelegramMessage(SendMessage message){
        return request.sendTelegramMessageObj(message);
    }
    public SentMessage sendTelegramMessage(String text, Long chatId) {
        return request.sendTelegramMessageObj(new SendMessage()
                .setText(text)
                .setChat_id(chatId));
    }
    public SentMessage editTelegramMessage(EditMessageText msg){
        return request.editTelegramMessageText(msg);
    }
    public SentMessage editTelegramMessage(Long chatId, Long messageId, String text){
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

    public void sendAndLogErrorMsg(@NotNull String errorCode, String errorMessage) {
            sendBackTelegramTextMessage("Error code: "+errorCode+".");
            log.warn("Error code: "+errorCode+". "+errorMessage);
    }

    public SentMessage sendBackTelegramTextMessage(String text) {
        if (this.originalSender == null) {
            sendTelegramMessage("Error code: MM.sBTTM001.",ME);
            log.warn("Error code: MM.sBTTM001. No original sender in MessageManager. Original text to send back: "+text);
            throw new RuntimeException("Error code: MM.sBTTM001.");
        }else {
            return request.sendTelegramMessageObj(new SendMessage()
                    .setText(text)
                    .setChat_id(originalSender.getId()));
        }
    }
    public void sendQuizResultInfo(Quiz quiz, Long whoTo) {
        if (quiz.getSuccess() == true) {
            sendTelegramMessage("Dobra odpowiedź na pytanie: " + quiz.getQuestion(), whoTo);
        } else if (quiz.getSuccess() == false) {
            sendTelegramMessage("Zła odpowiedź na pytanie: " + quiz.getQuestion() + " Odpowiedź: " + quiz.getLastAnswer(), whoTo);
        }
    }

    public SentMessage greetFirstTime() {
        if (originalSender.getLast_name() == null) {
            return sendBackTelegramTextMessage("Hello " + originalSender.getFirst_name() + "! Nice to see you! Hope You'll have a good time =]");
        } else {
            return sendBackTelegramTextMessage("Hello " + originalSender.getFirst_name() + " " + originalSender.getLast_name() + "! Nice to see you! Hope You'll have a good time =]");
        }
    }

    @PreDestroy
    private void messageOnQuit(){
        sendTelegramMessage("To już jest koniec \n T: "+ Utils.getCurrentDateTime(),getME());
        sendTelegramMessage("Chwilowa przerwa w działaniu bota. Wrócę za chwilę ;) \n Bot jest wyłączony.", getYASIA());
    }
}
