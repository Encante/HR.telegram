package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.RemoteRequest.RemoteRequest;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.*;
import net.ddns.encante.telegram.HR.persistence.repository.WebhookUpdateRepository;
import net.ddns.encante.telegram.HR.persistence.service.QuizService;
import net.ddns.encante.telegram.HR.persistence.service.WebhookUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class RequestHandler {
Gson gson = new GsonBuilder().setPrettyPrinting().create();
@Autowired
RemoteRequest request;
@Resource(name = "webhookUpdateService")
private WebhookUpdateService webhookUpdateService;
@Resource(name = "quizService")
private QuizService quizService;
@Autowired
private WebhookUpdateRepository webhookUpdateRepository;
private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
private final Long JA = 5580797031L;
private final Long YASIA = 566760042L;

//        when receiving message:
    @PostMapping("/HR4telegram")
    public String postHandler(@RequestBody String content) {
//        log incoming update
        log.debug("INCOMING WEBHOOK UPDATE BODY:");
        log.debug(gson.toJson(JsonParser.parseString(content)));
//        do WebhookUpdate object from JSON
        WebhookUpdate update = gson.fromJson(content, WebhookUpdate.class);
//        store it in the DB
        webhookUpdateService.saveWebhookUpdate(update);
        //            check if it is callback
        if (update.getCallback_query() != null) {
//            check if it is a quiz message callback
            if(quizService.getQuizByMessageId(update.getCallback_query().getMessage().getMessage_id())!=null){
                Quiz quiz = quizService.getQuizByMessageId(update.getCallback_query().getMessage().getMessage_id());
                quizService.saveQuiz(quiz.resolveAnswer(update));
                request.editTelegramMessage(new EditMessage(update.getCallback_query(), update.getCallback_query().getMessage().getText() + " Twoja odpowiedź: "+quiz.getAnswer()));
                if (quiz.getSuccess() == true){
                    request.answerCallbackQuery(new AnswerCallbackQuery(update.getCallback_query().getId(),"Bardzo dobrze!",true));
                    sendTelegramTextMessage("Dobra odpowiedź! ;)",update.getCallback_query().getMessage().getChat().getId());
//                    send me an info about quiz
                    sendTelegramTextMessage("Dobra odpowiedź w quizie: " + quiz.getQuestion(),JA);
                }
                else if (quiz.getSuccess() == false){
                    request.answerCallbackQuery(new AnswerCallbackQuery(update.getCallback_query().getId(),"Zła odpowiedź!",true));
                    sendTelegramTextMessage("Niestety zła odpowiedź :(",update.getCallback_query().getMessage().getChat().getId());
                    //                    send me an info about quiz
                    sendTelegramTextMessage("Zła odpowiedź w quizie: " + quiz.getQuestion()+" odpowiedź: "+quiz.getAnswer(),JA);
                }
            }
            else {
//            answer callback query
                request.answerCallbackQuery(new AnswerCallbackQuery(update.getCallback_query().getId(), "Callback Answer!", false));
//            delete keyboard after pressing a key
                request.editTelegramMessage(new EditMessage(update.getCallback_query()));
//            send me a message with callback
//            request.sendTelegramMessage(new SendMessage()
//                    .setText("Callback received! T: "
//                            + Utils.getCurrentDateTime()
//                            + "FROM: "
//                            + update.getCallback_query().getFrom().getFirst_name()
//                            + " "
//                            + update.getCallback_query().getFrom().getLast_name()
//                            + " CALLBACK DATA: "
//                            + update.getCallback_query().getData())
//                    .toMe());
            }
        }


//      check if have any message
        if (update.getMessage() != null) {
//      check if incoming message have any text
            if (update.getMessage().getText() != null) {
//        check if incoming message have any and if there is do commands:
                if (update.getMessage().getText().charAt(0) == '/') {
                    String[] commands = update.getMessage().getText().split(" ");
                    switch (commands[0]) {
                        case "/hi" -> sendTelegramTextMessage("Hello " + update.getMessage().getFrom().getFirst_name() + "!", update.getMessage().getFrom().getId());
                        case "/sm" -> {
                            if (commands.length < 3) {//command content validation
                                sendBadCommandWarning();
                            } else {
                                if (commands[1].equalsIgnoreCase("m")) {
                                    sendTelegramTextMessage(update.getMessage().getText().substring(6),5580797031L);
                                }
                                if (commands[1].equalsIgnoreCase("y")) {
                                    sendTelegramTextMessage(update.getMessage().getText().substring(6), 566760042L);
                                }
                            }
                        }
                        case "/smi" -> {
                            String[] names = {"Inline", "she", "goes"};
                            request.sendTelegramMessage(new SendMessage()
                                    .setText("Inline message")
                                    .setReply_markup(new InlineKeyboardMarkup.KeyboardBuilder(3, 1, names).build())
                                    .setChat_id(update.getMessage().getChat().getId()));
                        }
                        case "/smq" -> {
                            if (commands.length<6){
                                sendBadCommandWarning();
                            }
                            else {
                                Quiz quiz = new Quiz("Command line Quiz test", commands[2], commands[3], commands[4], commands[5], commands[2]);
                                quizService.saveQuiz(quiz);
                                request.sendTelegramMessage(quiz.createQuizMessageFromCommand(update));
                            }
                        }
                        case "/testq" ->{
                            sendQuizToMe();
                        }
                        case "/testqy" ->{
                            sendQuizToYasia();
                        }
                        case "/smk" -> {
                            String[] names = {"Reply", "she", "goes"};
                            request.sendTelegramMessage(new SendMessage()
                                    .setText("Message with keyboard")
                                    .setReply_markup(new ReplyKeyboardMarkup.KeyboardBuilder(3, 1, names).build())
                                    .setChat_id(update.getMessage().getChat().getId()));
                        }
                        case "/rmk" -> {
                            request.sendTelegramMessage(new SendMessage()
                                    .setText("Keyboard removed! Have fun you little shmuck ;)")
                                    .setChat_id(update.getMessage().getChat().getId())
                                    .setReply_markup(new ReplyKeyboardRemove()));
                        }
                        case "/searchUserById" -> {
                            if (commands.length > 1){
                                if (webhookUpdateRepository.findUserEntityByUserId(Long.decode(commands[1])) != null) {
                                    sendTelegramTextMessage("To " + webhookUpdateRepository.findUserEntityByUserId(Long.decode(commands[1])).getFirstName(),update.getMessage().getFrom().getId());
                                }
                                else sendTelegramTextMessage("User with id " + commands[1] + " not in DB!",update.getMessage().getFrom().getId());
                            }
                        }
                    }
                }
                //        if not from me, send message to me
                if (update.getMessage().getFrom().getId().equals(JA)) {
                    log.debug("Wiadomość ode mnie.");
                }
                else {
                    sendTelegramTextMessage("New message! T: " + Utils.getCurrentDateTime()
                            + "  FROM: "
                            + update.getMessage().getFrom().getFirst_name()
                            + " "
                            + update.getMessage().getFrom().getLast_name()
                            + "  CHAT ID: "
                            + update.getMessage().getChat().getId()
                            + "  CONTENT: "
                            + update.getMessage().getText(),JA);
                }
                return "200";
            } else {
//            if no text send me an info
                sendTelegramTextMessage("New message! T: " + Utils.getCurrentDateTime()
                        + "  FROM: "
                        + update.getMessage().getFrom().getFirst_name()
                        + " "
                        + update.getMessage().getFrom().getLast_name()
                        + "  CHAT ID: "
                        + update.getMessage().getChat().getId()
                        + " But it has no text!", JA);
                return "200";
            }
        }
//        update not contains message object
        else return "not a message";
    }
//
//    sending Quiz on schedule
//
    @Async
    @Scheduled(cron = "0 0 8-18 ? * *")
    public void sendQuizToYasia(){

//                            get next not sent quiz from db
        Quiz quiz = quizService.getNextQuizToSendFromDb();
//                            send quiz message
        SentMessage sentQuizMessage = request.sendTelegramMessage(quiz.createMessage(YASIA));
//                            update quiz obj
        quiz.setMessageId(sentQuizMessage.getResult().getMessage_id());
        quiz.setDateSent(sentQuizMessage.getResult().getDate());
        quiz.setAnswer(null);
//                            save updated quiz to db
        quizService.saveQuiz(quiz);
        sendTelegramTextMessage("Quiz "+ quiz.getQuestion() +"wysłany", JA);
        log.debug("Quiz "+quiz.getQuizId()+ " wyslany do Yasi!<<<<<<<<<");
    }

    public void sendQuizToMe(){
        //                            get next not sent quiz from db
        Quiz quiz = quizService.getNextQuizToSendFromDb();
//                            send quiz message
        SentMessage sentQuizMessage = request.sendTelegramMessage(quiz.createMessage(JA));
//                            update quiz obj
        quiz.setMessageId(sentQuizMessage.getResult().getMessage_id());
        quiz.setDateSent(sentQuizMessage.getResult().getDate());
        quiz.setAnswer(null);
//                            save updated quiz to db
        quizService.saveQuiz(quiz);
        log.debug("Quiz "+quiz.getQuizId()+ " wyslany do mnie!<<<<<<");
    }

    private SentMessage sendTelegramTextMessage (String text, Long chatId){
        return request.sendTelegramMessage(new SendMessage()
                .setText(text)
                .setChat_id(chatId));
    }
    private SentMessage sendBadCommandWarning (){
        return sendTelegramTextMessage("WARNING! BAD COMMAND!", 5580797031L);
    }
}