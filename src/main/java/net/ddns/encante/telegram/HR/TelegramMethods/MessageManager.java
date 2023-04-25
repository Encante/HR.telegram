package net.ddns.encante.telegram.HR.TelegramMethods;

import kong.unirest.Unirest;
import lombok.Data;
import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.RemoteRequest.UnirestRequest;
import net.ddns.encante.telegram.HR.TelegramObjects.SentMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.User;
import net.ddns.encante.telegram.HR.persistence.service.QuizService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Data
public class MessageManager {
    @Autowired
    UnirestRequest request;
    @Autowired
    QuizService quizService;
    private static final Logger log = LoggerFactory.getLogger(MessageManager.class);
    User originalSender;
    private final Long ME = 5580797031L;
    private final Long YASIA = 566760042L;
    private final Long CHOMIK = 6182762959L;

    public void sendQuizToId(Long chatId){
//                            get next not sent quiz from db
            Quiz quiz = quizService.getNextQuizToSendFromDb();
//                            send quiz message
            SentMessage sentQuizMessage = request.sendTelegramMessageObj(quizService.createMessage(chatId, quiz));
//                            update quiz obj
            quiz.setMessageId(sentQuizMessage.getResult().getMessage_id());
            quiz.setDateSent(sentQuizMessage.getResult().getDate());
            quiz.setLastAnswer(null);
//                            save updated quiz to db
            quizService.saveQuiz(quiz);
            sendTelegramTextMessage("Quiz "+ quiz.getQuestion() +" wysłany", ME);
            log.debug("Quiz "+quiz.getQuizId()+ " wyslany <<<<<<<<<");
    }
    @Async
    @Scheduled(cron = "0 0 8-18 ? * *")
    public void sendQuizToYasia(){
        sendQuizToId(YASIA);
    }
    public SentMessage sendTelegramObjAsMessage(SendMessage message){
        return request.sendTelegramMessageObj(message);
    }
    public SentMessage sendTelegramTextMessage(String text, Long chatId) {
        return request.sendTelegramMessageObj(new SendMessage()
                .setText(text)
                .setChat_id(chatId));
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
}
