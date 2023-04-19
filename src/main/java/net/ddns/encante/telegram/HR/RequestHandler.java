package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.RemoteRequest.UnirestRequest;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.MessageManager;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.*;
import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import net.ddns.encante.telegram.HR.persistence.repository.QuizRepository;
import net.ddns.encante.telegram.HR.persistence.repository.WebhookUpdateRepository;
import net.ddns.encante.telegram.HR.persistence.service.HueAuthorizationService;
import net.ddns.encante.telegram.HR.persistence.service.QuizService;
import net.ddns.encante.telegram.HR.persistence.service.WebhookUpdateService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class RequestHandler {
Gson gson = new GsonBuilder().setPrettyPrinting().create();
@Autowired
UnirestRequest request;
@Resource(name = "webhookUpdateService")
private WebhookUpdateService webhookUpdateService;
@Resource(name = "quizService")
private QuizService quizService;
@Resource(name = "hueAuthorizationService")
private HueAuthorizationService hueAuthorizationService;
@Autowired
private WebhookUpdateRepository webhookUpdateRepository;
@Autowired
private QuizRepository quizRepository;
@Autowired
private MessageManager msgManager;
private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
private String[] commands;


    //        when receiving message:
    @PostMapping("/HR4telegram")
    public String postHandler(@RequestBody WebhookUpdate update) {// do WebhookUpdate object from JSON
//        log incoming update
        log.debug("INCOMING WEBHOOK UPDATE BODY:");
//        log.debug(gson.toJson(JsonParser.parseString(content)));
        log.debug(gson.toJson(update));
//        store update it in the DB
        webhookUpdateService.saveWebhookUpdate(update);
//            check if it is callback
        if (update.getCallback_query() != null) {
            //        just little helpful var with chatID of who send msg
            msgManager.setOriginalSender(update.getCallback_query().getFrom());
//            check if it is a quiz message callback and resolve quiz
            if(quizService.getQuizByMessageId(update.getCallback_query().getMessage().getMessage_id())!=null){
                Quiz quiz = quizService.getQuizByMessageId(update.getCallback_query().getMessage().getMessage_id());
//              actually resolve answer
                quizService.saveQuiz(quiz.resolveAnswer(update));
//              edit message: add answer
                request.editTelegramMessage(new EditMessage(update.getCallback_query(), update.getCallback_query().getMessage().getText() +" Twoja odpowiedź: "+quiz.getLastAnswer()));
//                add popup message with response to answer
                request.answerCallbackQuery(quiz.getReactionForAnswerCallback());
//                send reply to message with answer
                request.sendTelegramMessageObj(quiz.getReactionForAnswerMessage());
//                    send me an info about quiz
                    msgManager.sendQuizResultInfo(quiz, msgManager.getME());
            }
            else {
//            answer callback query
                request.answerCallbackQuery(new AnswerCallbackQuery(update.getCallback_query().getId(), "Callback Answer!", false));
//            delete keyboard after pressing a key
                request.editTelegramMessage(new EditMessage(update.getCallback_query()));
            }
            //      check if have any message
        } else if (update.getMessage() != null) {
            //        just little helpful var with chatID of who send msg
            msgManager.setOriginalSender(update.getMessage().getFrom());
//      check if incoming message have any text
            if (update.getMessage().getText() != null) {
//        check if incoming message have any and if there is do commands:
                if (update.getMessage().getText().charAt(0) == '/') {
                    this.commands = update.getMessage().getText().split(" ");
                    switch (commands[0]) {
//                        basic command to check if bot is running
                        case "/hi" -> msgManager.greet();
                        case "/commands" ->msgManager.sendBackTelegramTextMessage("/hi /commands /start /sm");
                        case "/start" -> msgManager.greetFirstTime();
//                        send message through bot
                        case "/sm" -> {
                            if (checkCommandLenght(3, "/sm")) {
//                                send message to me - checking purposes
                                if (commands[1].equalsIgnoreCase("m")) {
                                    msgManager.sendTelegramTextMessage(update.getMessage().getText().substring(6),msgManager.getME());
                                }
//                                send message to Yas
                                if (commands[1].equalsIgnoreCase("y")) {
                                    msgManager.sendTelegramTextMessage(update.getMessage().getText().substring(6), msgManager.getYASIA());
                                }
//                                send msg to chom
                                if (commands[1].equalsIgnoreCase("c")){
                                    msgManager.sendTelegramTextMessage(update.getMessage().getText().substring(6), msgManager.getCHOMIK());
                                }
                            }
                        }
                        case "/smi" -> {
                            String[] names = {"Inline", "she", "goes"};
                            request.sendTelegramMessageObj(new SendMessage()
                                    .setText("Inline message")
                                    .setReply_markup(new InlineKeyboardMarkup.KeyboardBuilder(3, 1, names).build())
                                    .setChat_id(msgManager.getOriginalSender().getId()));
                        }
                        case "/smq" -> {
                            if (checkCommandLenght(6, "/smq")){
                                Quiz quiz = new Quiz("Command line Quiz test", commands[2], commands[3], commands[4], commands[5], commands[2]);
                                quizService.saveQuiz(quiz);
                                request.sendTelegramMessageObj(quiz.createQuizMessageFromCommand(update));
                            }
                        }
                        case "/quizme" -> sendQuizToMe();

                        case "/quizyas" -> sendQuizToYasia();
                        case "/quizid" -> {
                            if (checkCommandLenght(2, "/quizid")){
                                sendQuizToId(Long.decode(commands[1]));
                            }
                        }

                        case "/hmql" -> msgManager.sendTelegramTextMessage("Zostało "+quizRepository.findAllQuizEntitiesToSend().size()+" pytań.", msgManager.getME());
                        case "/smk" -> {
                            String[] names = {"Reply", "she", "goes"};
                            request.sendTelegramMessageObj(new SendMessage()
                                    .setText("Message with keyboard")
                                    .setReply_markup(new ReplyKeyboardMarkup.KeyboardBuilder(3, 1, names).build())
                                    .setChat_id(msgManager.getOriginalSender().getId()));
                        }
                        case "/rmk" -> {
                            request.sendTelegramMessageObj(new SendMessage()
                                    .setText("Keyboard removed! Have fun you little shmuck ;)")
                                    .setChat_id(msgManager.getOriginalSender().getId())
                                    .setReply_markup(new ReplyKeyboardRemove()));
                        }
                        case "/searchUserById" -> {
                            if (checkCommandLenght(2,"/searchUserById")){
                                if (webhookUpdateRepository.findUserEntityByUserId(Long.decode(commands[1])) != null) {
                                    msgManager.sendBackTelegramTextMessage("To " + webhookUpdateRepository.findUserEntityByUserId(Long.decode(commands[1])).getFirstName());
                                }
                                else msgManager.sendBackTelegramTextMessage("User with id " + commands[1] + " not in DB!");
                            }
                        }
                        case "/hueapp" -> {
//                            command lenght check
                            if (checkCommandLenght(3,"/hueapp")) {
                                if (commands[1].equalsIgnoreCase("link")) {
                                    if (checkAuthorizationForDisplayName(commands[2],"/hueapp link")) {
                                        HueAuthorizationEntity authorization = hueAuthorizationService.getAuthorizationForDisplayName(commands[2]);
                                        msgManager.sendBackTelegramTextMessage(authorization.generateAuthorizationLink());
                                        hueAuthorizationService.saveOrUpdateAuthorizationBasedOnClientId(authorization);
                                    }
                                }
                                if (commands[1].equalsIgnoreCase("checktokens")) {
//                                        authorization for display name check
                                    if (checkAuthorizationForDisplayName(commands[2], "/hueapp checktokens")) {
                                        HueAuthorizationEntity authorization = hueAuthorizationService.getAuthorizationForDisplayName(commands[2]);
                                        if (authorization.getTokens().getAccess_token()!=null && authorization.getUsername()!=null){

                                        }
                                    }
                                }
                                if (commands[1].equalsIgnoreCase("add")){
                                    if (checkCommandLenght(5, "/hueapp add")){
                                    HueAuthorizationEntity authorization = new HueAuthorizationEntity();
                                    authorization.setClientId(commands[2]);
                                    authorization.setClientSecret(commands[3]);
                                    authorization.setDisplayName(commands[4]);
                                    hueAuthorizationService.saveOrUpdateAuthorizationBasedOnClientId(authorization);
                                    msgManager.sendBackTelegramTextMessage("Hue App "+authorization.getDisplayName()+ " added to DB.");
                                    }
                                }
                                if (commands[1].equalsIgnoreCase("searchByName")){
                                    if (hueAuthorizationService.getAuthorizationForDisplayName(commands[2])!= null) {
                                        HueAuthorizationEntity entity =
                                        hueAuthorizationService.getAuthorizationForDisplayName(commands[2]);
                                        msgManager.sendBackTelegramTextMessage("App with this Id already in DB with client ID: "+entity.getClientId());
                                    }else msgManager.sendBackTelegramTextMessage("No app with this name added");
                                }
                            }
                        }
                    }
                }
                //        if not from me, send message to me
                if (update.getMessage().getFrom().getId().equals(msgManager.getME())) {
                    log.debug("Wiadomość do bota ode mnie.");
                }
                else {
                    msgManager.sendTelegramTextMessage("New message! T: " + Utils.getCurrentDateTime()
                            + "  FROM: "
                            + msgManager.getOriginalSender().getFirst_name()
                            + " "
                            + update.getMessage().getFrom().getLast_name()
                            + "  CHAT ID: "
                            + msgManager.getOriginalSender().getId()
                            + "  CONTENT: "
                            + update.getMessage().getText(), msgManager.getME());
                }
                return "200";
            } else {
//            if no text send me an info
                msgManager.sendTelegramTextMessage("New message! T: " + Utils.getCurrentDateTime()
                        + "  FROM: "
                        + msgManager.getOriginalSender().getFirst_name()
                        + " "
                        + msgManager.getOriginalSender().getLast_name()
                        + "  CHAT ID: "
                        + msgManager.getOriginalSender().getId()
                        + " But it has no text!", msgManager.getME());
                return "200";
            }
            //        update not contains message object and is not a callback
        }else msgManager.sendTelegramTextMessage("New message. T: " + Utils.getCurrentDateTime()
                + "  FROM: "
                + msgManager.getOriginalSender().getFirst_name()
                + " "
                + msgManager.getOriginalSender().getLast_name()
                + "  CHAT ID: "
                + msgManager.getOriginalSender().getId()
                + " But it has no message object nor it's a callback.", msgManager.getME());
        return "200";
    }

    @GetMapping("/hue/code")
    public void hueAuthentication(@RequestParam String code, @RequestParam String state){
        log.debug("New Hue code retrieved!"+code);
        if (hueAuthorizationService.getAuthorizationForState(state)!= null) {
            HueAuthorizationEntity authorization = hueAuthorizationService.getAuthorizationForState(state);
            authorization.setCode(code);
            hueAuthorizationService.saveOrUpdateAuthorizationBasedOnClientId(request.requestHueAuthentication(authorization));
            msgManager.sendTelegramTextMessage("Tokens retrieved! App " + authorization.getDisplayName() + " authorized!", msgManager.getME());
        }else {
            log.warn("No authorization found for state '" + state + "'.");
            msgManager.sendTelegramTextMessage("No authorization found for state '" + state + "'.", msgManager.getME());
        }
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
        SentMessage sentQuizMessage = request.sendTelegramMessageObj(quiz.createMessage(msgManager.getYASIA()));
//                            update quiz obj
        quiz.setMessageId(sentQuizMessage.getResult().getMessage_id());
        quiz.setDateSent(sentQuizMessage.getResult().getDate());
        quiz.setLastAnswer(null);
//                            save updated quiz to db
        quizService.saveQuiz(quiz);
        msgManager.sendTelegramTextMessage("Quiz "+ quiz.getQuestion() +" wysłany", msgManager.getME());
        log.debug("Quiz "+quiz.getQuizId()+ " wyslany do Yasi!<<<<<<<<<");
    }

    public void sendQuizToMe(){
        //                            get next not sent quiz from db
        Quiz quiz = quizService.getNextQuizToSendFromDb();
//                            send quiz message
        SentMessage sentQuizMessage = request.sendTelegramMessageObj(quiz.createMessage(msgManager.getME()));
//                            update quiz obj
        quiz.setMessageId(sentQuizMessage.getResult().getMessage_id());
        quiz.setDateSent(sentQuizMessage.getResult().getDate());
        quiz.setLastAnswer(null);
//                            save updated quiz to db
        quizService.saveQuiz(quiz);
        log.debug("Quiz "+quiz.getQuizId()+ " wyslany do mnie!<<<<<<");
    }
    public void sendQuizToId (Long chatId){
        //                            get next not sent quiz from db
        Quiz quiz = quizService.getNextQuizToSendFromDb();
//                            send quiz message
        SentMessage sentQuizMessage = request.sendTelegramMessageObj(quiz.createMessage(chatId));
//                            update quiz obj
        quiz.setMessageId(sentQuizMessage.getResult().getMessage_id());
        quiz.setDateSent(sentQuizMessage.getResult().getDate());
        quiz.setLastAnswer(null);
//                            save updated quiz to db
        quizService.saveQuiz(quiz);
        log.debug("Quiz "+quiz.getQuizId()+ " wyslany do chatId "+chatId+" <<<<<<");
    }
//
//              PRIVATE ONLY
//
    Boolean checkCommandLenght (int atLeast, String invoker){
        if (commands.length >= atLeast){
            return true;
        }else {
            msgManager.sendAndLogErrorMsg("Bad command lenght - insuficient parameters from command "+ invoker);
            return false;
        }
    }
    private boolean checkAuthorizationForDisplayName(String displayName, String invoker){
        if (hueAuthorizationService.getAuthorizationForDisplayName(displayName) != null) {
            return true;
        } else {
            msgManager.sendAndLogErrorMsg("No authorization for app with name '"+displayName+"' in DB. Invoker: "+invoker);
            return false;
        }
    }
    private boolean checkAndRefreshHueAuthorization (@NotNull HueAuthorizationEntity authorization) {
        //                                            authorization token and username check
        if (authorization.getTokens().getAccess_token() != null && authorization.getUsername() != null) {
//                                                check if access token and username is valid
            if (request.hueGetResourceDevice(authorization) != null && request.hueGetResourceDevice(authorization).getErrors() == null) {
                log.debug("Tokens for " + authorization.getDisplayName() + " checked and valid.");
                return true;
            } else{
                return false;
            }
        }else {
            String err = "ERROR. Access token or username is null. Invoker: checkAndRefreshHueAuthorization";
            msgManager.sendAndLogErrorMsg(err);
            throw new RuntimeException(err);
        }
    }
}
