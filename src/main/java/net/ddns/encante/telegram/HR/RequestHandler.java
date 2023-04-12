package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.RemoteRequest.RemoteRequest;
import net.ddns.encante.telegram.HR.RemoteRequest.UnirestRequest;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.*;
import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import net.ddns.encante.telegram.HR.persistence.repository.QuizRepository;
import net.ddns.encante.telegram.HR.persistence.repository.WebhookUpdateRepository;
import net.ddns.encante.telegram.HR.persistence.service.HueAuthorizationService;
import net.ddns.encante.telegram.HR.persistence.service.QuizService;
import net.ddns.encante.telegram.HR.persistence.service.WebhookUpdateService;
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
private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
private final Long ME = 5580797031L;
private final Long YASIA = 566760042L;
private final Long CHOMIK = 6182762959L;
private User backToSender;

    //        when receiving message:
    @PostMapping("/HR4telegram")
    public String postHandler(@RequestBody WebhookUpdate update) {
//        log incoming update
        log.debug("INCOMING WEBHOOK UPDATE BODY:");
//        log.debug(gson.toJson(JsonParser.parseString(content)));
        log.debug(gson.toJson(update));
//        do WebhookUpdate object from JSON
//        WebhookUpdate update = gson.fromJson(content, WebhookUpdate.class);
//        store it in the DB
        webhookUpdateService.saveWebhookUpdate(update);
        //            check if it is callback
        if (update.getCallback_query() != null) {
            //        just little helpful var with chatID of who send msg
            this.backToSender = update.getMessage().getFrom();
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
                request.sendTelegramMessage(quiz.getReactionForAnswerMessage());
//                    send me an info about quiz
                    sendQuizResultInfo(quiz,ME);
            }
            else {
//            answer callback query
                request.answerCallbackQuery(new AnswerCallbackQuery(update.getCallback_query().getId(), "Callback Answer!", false));
//            delete keyboard after pressing a key
                request.editTelegramMessage(new EditMessage(update.getCallback_query()));
            }
        }


//      check if have any message
        if (update.getMessage() != null) {
            //        just little helpful var with chatID of who send msg
            this.backToSender = update.getMessage().getFrom();
//      check if incoming message have any text
            if (update.getMessage().getText() != null) {
//        check if incoming message have any and if there is do commands:
                if (update.getMessage().getText().charAt(0) == '/') {
                    String[] commands = update.getMessage().getText().split(" ");
                    switch (commands[0]) {
//                        basic command to check if bot is running
                        case "/hi" -> greet(update.getMessage().getFrom());
                        case "/commands" ->sendBackTelegramTextMessage("/hi /commands /start /sm");
                        case "/start" -> greetFirstTime(update.getMessage().getFrom());
//                        send message through bot
                        case "/sm" -> {
                            if (checkCommandLenght(3, commands, "/sm", backToSender)) {
//                                send message to me - checking purposes
                                if (commands[1].equalsIgnoreCase("m")) {
                                    sendTelegramTextMessage(update.getMessage().getText().substring(6),ME);
                                }
//                                send message to Yas
                                if (commands[1].equalsIgnoreCase("y")) {
                                    sendTelegramTextMessage(update.getMessage().getText().substring(6), YASIA);
                                }
//                                send msg to chom
                                if (commands[1].equalsIgnoreCase("c")){
                                    sendTelegramTextMessage(update.getMessage().getText().substring(6), CHOMIK);
                                }
                            }
                        }
                        case "/smi" -> {
                            String[] names = {"Inline", "she", "goes"};
                            request.sendTelegramMessage(new SendMessage()
                                    .setText("Inline message")
                                    .setReply_markup(new InlineKeyboardMarkup.KeyboardBuilder(3, 1, names).build())
                                    .setChat_id(backToSender));
                        }
                        case "/smq" -> {
                            if (checkCommandLenght(6, commands, "/smq",backToSender)){
                                Quiz quiz = new Quiz("Command line Quiz test", commands[2], commands[3], commands[4], commands[5], commands[2]);
                                quizService.saveQuiz(quiz);
                                request.sendTelegramMessage(quiz.createQuizMessageFromCommand(update));
                            }
                        }
                        case "/quizme" -> sendQuizToMe();

                        case "/quizyas" -> sendQuizToYasia();
                        case "/quizid" -> {
                            if (checkCommandLenght(2, commands,"/quizid",backToSender)){
                                sendQuizToId(Long.decode(commands[1]));
                            }
                        }

                        case "/hmql" -> sendTelegramTextMessage("Zostało "+quizRepository.findAllQuizEntitiesToSend().size()+" pytań.", ME);
                        case "/smk" -> {
                            String[] names = {"Reply", "she", "goes"};
                            request.sendTelegramMessage(new SendMessage()
                                    .setText("Message with keyboard")
                                    .setReply_markup(new ReplyKeyboardMarkup.KeyboardBuilder(3, 1, names).build())
                                    .setChat_id(backToSender));
                        }
                        case "/rmk" -> {
                            request.sendTelegramMessage(new SendMessage()
                                    .setText("Keyboard removed! Have fun you little shmuck ;)")
                                    .setChat_id(backToSender)
                                    .setReply_markup(new ReplyKeyboardRemove()));
                        }
                        case "/searchUserById" -> {
                            if (checkCommandLenght(2,commands,"/searchUserById",backToSender)){
                                if (webhookUpdateRepository.findUserEntityByUserId(Long.decode(commands[1])) != null) {
                                    sendBackTelegramTextMessage("To " + webhookUpdateRepository.findUserEntityByUserId(Long.decode(commands[1])).getFirstName());
                                }
                                else sendBackTelegramTextMessage("User with id " + commands[1] + " not in DB!");
                            }
                        }
                        case "/hueapp" -> {
//                            command lenght check
                            if (checkCommandLenght(3,commands,"/hueapp",backToSender)) {
                                if (commands[1].equalsIgnoreCase("link")) {
                                    if (checkAuthorizationForDisplayName(commands[2],"/hueapp link",backToSender)) {
                                        HueAuthorizationEntity authorization = hueAuthorizationService.getAuthorizationForDisplayName(commands[2]);
                                        sendBackTelegramTextMessage(authorization.generateAuthorizationLink());
                                        hueAuthorizationService.saveOrUpdateAuthorizationBasedOnClientId(authorization);
                                    }
                                }
                                if (commands[1].equalsIgnoreCase("checktokens")) {
//                                        authorization for display name check
                                    if (checkAuthorizationForDisplayName(commands[2], "/hueapp checktokens",backToSender)) {
                                        HueAuthorizationEntity authorization = hueAuthorizationService.getAuthorizationForDisplayName(commands[2]);
                                        if (authorization.getTokens().getAccess_token()!=null && authorization.getUsername()!=null){

                                        }
                                    }
                                }
                                if (commands[1].equalsIgnoreCase("add")){
                                    if (checkCommandLenght(5, commands,"/hueapp add",backToSender)){
                                    HueAuthorizationEntity authorization = new HueAuthorizationEntity();
                                    authorization.setClientId(commands[2]);
                                    authorization.setClientSecret(commands[3]);
                                    authorization.setDisplayName(commands[4]);
                                    hueAuthorizationService.saveOrUpdateAuthorizationBasedOnClientId(authorization);
                                    sendBackTelegramTextMessage("Hue App "+authorization.getDisplayName()+ " added to DB.");
                                    }
                                }
                                if (commands[1].equalsIgnoreCase("searchByName")){
                                    if (hueAuthorizationService.getAuthorizationForDisplayName(commands[2])!= null) {
                                        HueAuthorizationEntity entity =
                                        hueAuthorizationService.getAuthorizationForDisplayName(commands[2]);
                                        sendBackTelegramTextMessage("App with this Id already in DB with client ID: "+entity.getClientId());
                                    }else sendBackTelegramTextMessage("No app with this name added");
                                }
                            }
                        }
                    }
                }
                //        if not from me, send message to me
                if (update.getMessage().getFrom().getId().equals(ME)) {
                    log.debug("Wiadomość do bota ode mnie.");
                }
                else {
                    sendTelegramTextMessage("New message! T: " + Utils.getCurrentDateTime()
                            + "  FROM: "
                            + update.getMessage().getFrom().getFirst_name()
                            + " "
                            + update.getMessage().getFrom().getLast_name()
                            + "  CHAT ID: "
                            + backToSender
                            + "  CONTENT: "
                            + update.getMessage().getText(), ME);
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
                        + backToSender
                        + " But it has no text!", ME);
                return "200";
            }
        }
//        update not contains message object
        else sendTelegramTextMessage("New message! T: " + Utils.getCurrentDateTime()
                + "  FROM: "
                + update.getMessage().getFrom().getFirst_name()
                + " "
                + update.getMessage().getFrom().getLast_name()
                + "  CHAT ID: "
                + backToSender
                + " But it has no message object", ME);
        return "200";
    }

    @GetMapping("/hue/code")
    public void hueAuthentication(@RequestParam String code, @RequestParam String state){
        log.debug("New Hue code retrieved!"+code);
        if (hueAuthorizationService.getAuthorizationForState(state)!= null) {
            HueAuthorizationEntity authorization = hueAuthorizationService.getAuthorizationForState(state);
            authorization.setCode(code);
            hueAuthorizationService.saveOrUpdateAuthorizationBasedOnClientId(request.requestHueAuthentication(authorization));
            sendTelegramTextMessage("Tokens retrieved! App " + authorization.getDisplayName() + " authorized!", ME);
        }else {
            log.warn("No authorization found for state '" + state + "'.");
            sendTelegramTextMessage("No authorization found for state '" + state + "'.", ME);
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
        SentMessage sentQuizMessage = request.sendTelegramMessage(quiz.createMessage(YASIA));
//                            update quiz obj
        quiz.setMessageId(sentQuizMessage.getResult().getMessage_id());
        quiz.setDateSent(sentQuizMessage.getResult().getDate());
        quiz.setLastAnswer(null);
//                            save updated quiz to db
        quizService.saveQuiz(quiz);
        sendTelegramTextMessage("Quiz "+ quiz.getQuestion() +" wysłany", ME);
        log.debug("Quiz "+quiz.getQuizId()+ " wyslany do Yasi!<<<<<<<<<");
    }

    public void sendQuizToMe(){
        //                            get next not sent quiz from db
        Quiz quiz = quizService.getNextQuizToSendFromDb();
//                            send quiz message
        SentMessage sentQuizMessage = request.sendTelegramMessage(quiz.createMessage(ME));
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
        SentMessage sentQuizMessage = request.sendTelegramMessage(quiz.createMessage(chatId));
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
    private class Checker{
        int atLeast;
        String[] command;
        String invoker;
        Long whoToInform;
        String displayName;

//        setting constant values
        Checker (String[] command, Long whoToInform){
            this.command = command;
            this.whoToInform = whoToInform;
        }
        Checker lenghtChecker (int atLeast, String invoker){
            this.atLeast = atLeast;
            this.invoker = invoker;
            return this;
        }
        Checker authChecker (String displayName, String invoker){
            this.displayName = displayName;
            this.invoker = invoker;
            return this;
        }
};
    private Boolean checkCommandLenght (int atLeast, String[] command, String invokerCommand, Long whoToInform){
        if (command.length >= atLeast){
            return true;
        }else {
            log.warn("Bad command lenght - insuficient parameters from command "+ invokerCommand);
            sendTelegramTextMessage("Bad command lenght - insuficient parameters from command "+ invokerCommand, whoToInform);
            return false;
        }
    }
    private Boolean checkAuthorizationForDisplayName(String displayName, String invoker, Long whoToInform){
        if (hueAuthorizationService.getAuthorizationForDisplayName(displayName) != null) {
            return true;
        } else {
            log.warn("No authorization for app with name '"+displayName+"' in DB. Invoker: "+invoker);
            sendTelegramTextMessage("No authorization for app with name '"+displayName+"' in DB. Invoker: "+invoker, whoToInform);
            return false;
        }
    }
    private Boolean checkAndRefreshHueAuthorization (HueAuthorizationEntity authorization) {
        //                                            authorization token and username check
        if (authorization.getTokens().getAccess_token() != null && authorization.getUsername() != null) {
//                                                check if access token and username is valid
            if (request.hueGetResourceDevice(authorization) != null && request.hueGetResourceDevice(authorization).getErrors() == null) {
                log.debug("Tokens for " + authorization.getDisplayName() + " checked and valid.");
            } else{};
//                                            do GET DEVICES REQUEST TO CHECK ACCESS TOKENS
        }
    }
    private SentMessage greetFirstTime(){
        if (backToSender!=null) {
            if (backToSender.getLast_name() != null) {
                return sendBackTelegramTextMessage("Hello " + backToSender.getFirst_name() + " " + backToSender.getLast_name() + "! Nice to see you! Hope You'll have a good time =]");
            } else {
                return sendBackTelegramTextMessage("Hello " + backToSender.getFirst_name() + "! Nice to see you! Hope You'll have a good time =]", backToSender.getId());
            }
        }else {
            log.warn("ERROR! backToSender is null. Invoker: greetFirstTime");
            sendTelegramTextMessage("ERROR! backToSender is null. Invoker: greetFirstTime",ME);
            throw new RuntimeException("ERROR! backToSender is null. Invoker: greetFirstTime");
        }
    }
    private SentMessage greet (){
        if (backToSender!= null) {
            if (backToSender.getLast_name() != null) {
                return sendBackTelegramTextMessage("Hey " + backToSender.getFirst_name() + " " + backToSender.getLast_name() + "! Have a nice day =]");
            } else {
                return sendTelegramTextMessage("Hello " + whoToGreet.getFirst_name() + "! Have a nice day =]", whoToGreet.getId());
            }
        }
    }
    private SentMessage sendTelegramTextMessage (String text, Long chatId){
        return request.sendTelegramMessage(new SendMessage()
                .setText(text)
                .setChat_id(chatId));
    }
    private SentMessage sendBackTelegramTextMessage (String text){
        if (this.backToSender!=null) {
            return request.sendTelegramMessage(new SendMessage()
                    .setText(text)
                    .setChat_id(backToSender.getId()));
        }else {
            logSendThrowError("ERROR. No backToSender set! Text for debug: "+text);
        }
    }
    private void sendQuizResultInfo(Quiz quiz, Long whoTo){
        if (quiz.getSuccess() == true){
//            String msg = "Dobra odpowiedź"
//            sendTelegramTextMessage(StringEscapeUtils.escapeJava()"Dobra odpowiedź!")
            sendTelegramTextMessage("Dobra odpowiedź na pytanie: " + quiz.getQuestion(), whoTo);
        } else if (quiz.getSuccess() == false) {
            sendTelegramTextMessage("Zła odpowiedź na pytanie: " + quiz.getQuestion() + " Odpowiedź: " + quiz.getLastAnswer(),whoTo);
        }
    }
    private void logSendThrowError(String error){
        log.warn(error);
        sendTelegramTextMessage(error,ME);
        throw new RuntimeException(error);
    }    
}