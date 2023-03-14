package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.RemoteRequest.RemoteRequest;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.*;
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
RemoteRequest request;
@Resource(name = "webhookUpdateService")
private WebhookUpdateService webhookUpdateService;
@Resource(name = "quizService")
private QuizService quizService;
@Resource(name = "hueTokensService")
private HueAuthorizationService hueAuthorizationService;
@Autowired
private WebhookUpdateRepository webhookUpdateRepository;
@Autowired
private QuizRepository quizRepository;
private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
private final Long ME = 5580797031L;
private final Long YASIA = 566760042L;


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
            Long backToSender = update.getCallback_query().getMessage().getChat().getId();
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
            //        just little helpful var with chatID of who send msg
            Long backToSender = update.getMessage().getChat().getId();
//      check if incoming message have any text
            if (update.getMessage().getText() != null) {
//        check if incoming message have any and if there is do commands:
                if (update.getMessage().getText().charAt(0) == '/') {
                    String[] commands = update.getMessage().getText().split(" ");
                    switch (commands[0]) {
//                        basic command to check if bot is running
                        case "/hi" -> greet(update.getMessage().getFrom());
                        case "/start" -> greetFirstTime(update.getMessage().getFrom());
//                        send message through bot
                        case "/sm" -> {
                            if (commands.length < 3) {//command content validation
                                sendBadCommandWarning();
                            } else {
//                                send message to me - checking purposes
                                if (commands[1].equalsIgnoreCase("m")) {
                                    sendTelegramTextMessage(update.getMessage().getText().substring(6),ME);
                                }
//                                send message to Yas
                                if (commands[1].equalsIgnoreCase("y")) {
                                    sendTelegramTextMessage(update.getMessage().getText().substring(6), YASIA);
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
                            if (commands.length<6){
                                sendBadCommandWarning();
                            }
                            else {
                                Quiz quiz = new Quiz("Command line Quiz test", commands[2], commands[3], commands[4], commands[5], commands[2]);
                                quizService.saveQuiz(quiz);
                                request.sendTelegramMessage(quiz.createQuizMessageFromCommand(update));
                            }
                        }
                        case "/quizme" -> sendQuizToMe();

                        case "/quizyas" -> sendQuizToYasia();
                        case "/quizid" -> {
                            if (commands.length < 2) sendBadCommandWarning();
                            else{
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
                if (update.getMessage().getFrom().getId().equals(ME)) {
                    log.debug("Wiadomość ode mnie.");
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
        else return "not a message";
    }

    @GetMapping("/hue/code")
    public void hueTokensCreator(@RequestParam String code){
        log.debug("New Hue code retrieved: "+code);
        sendTelegramTextMessage("New Hue code retrieved: "+code, ME);
//        request.
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
//
//
    private SentMessage greetFirstTime(User whoToGreet){
        if (whoToGreet.getLast_name() != null){
            return sendTelegramTextMessage("Hello "+ whoToGreet.getFirst_name() + " " + whoToGreet.getLast_name() + "! Nice to see you! Hope You'll have a good time =]", whoToGreet.getId());
        }
        else {
            return sendTelegramTextMessage("Hello "+ whoToGreet.getFirst_name() + "! Nice to see you! Hope You'll have a good time =]", whoToGreet.getId());
        }
    }
    private SentMessage greet (User whoToGreet){
        if (whoToGreet.getLast_name() != null){
            return sendTelegramTextMessage("Hey "+ whoToGreet.getFirst_name() + " " + whoToGreet.getLast_name() + "! Have a nice day =]", whoToGreet.getId());
        }
        else {
            return sendTelegramTextMessage("Hello "+ whoToGreet.getFirst_name() + "! Have a nice day =]", whoToGreet.getId());
        }
    }
    private SentMessage sendTelegramTextMessage (String text, Long chatId){
        return request.sendTelegramMessage(new SendMessage()
                .setText(text)
                .setChat_id(chatId));
    }
    private SentMessage sendBadCommandWarning (){
        return sendTelegramTextMessage("WARNING! BAD COMMAND!", 5580797031L);
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
}