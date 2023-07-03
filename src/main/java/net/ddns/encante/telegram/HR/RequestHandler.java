package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.MessageManager;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.ReplyKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.ReplyKeyboardRemove;
import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;
import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import net.ddns.encante.telegram.HR.persistence.repository.QuizRepository;
import net.ddns.encante.telegram.HR.persistence.repository.WebhookUpdateRepository;
import net.ddns.encante.telegram.HR.persistence.service.HueAuthorizationService;
import net.ddns.encante.telegram.HR.persistence.service.QuizService;
import net.ddns.encante.telegram.HR.persistence.service.WebhookUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class RequestHandler {
Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
    public void postHandler(@RequestBody WebhookUpdate update) {// do WebhookUpdate object from JSON
//        log incoming update
        log.debug("INCOMING WEBHOOK UPDATE BODY:");
//        log.debug(gson.toJson(JsonParser.parseString(content)));
        log.debug(gson.toJson(update));
//        store update in the DB
        webhookUpdateService.saveWebhookUpdate(update);
//            check if it is callback
        if (update.getCallback_query() != null) {
            //        initialise msgManager setting originalSender of message
            msgManager.setOriginalSender(update.getCallback_query().getFrom());
//            check if it is a quiz message callback and eventually resolve a quiz
            if(quizService.getQuizByCredentials(update.getCallback_query().getMessage().getMessage_id(),msgManager.getOriginalSender().getId())!=null){
//              actually resolve answer
                quizService.resolveQuizAnswer(update);
            }
            else {
//                nothing specific other than quiz is designed to be used by callbacks so now default behavior will be sending "Callback Answer" and deleting an inline keyboard
//            answer callback query
                msgManager.answerCallbackQuery(new AnswerCallbackQuery(update.getCallback_query().getId(), "Callback Answer!", false));
//            delete keyboard after pressing a key
                msgManager.editTelegramMessageText(update.getCallback_query().getFrom().getId(),update.getCallback_query().getMessage().getMessage_id(),update.getCallback_query().getMessage().getText());
            }
//      if it doesnt have callback querry, check if it has any message obj
        } else if (update.getMessage() != null) {
//        just little helpful var with chatID of who send msg
            msgManager.setOriginalSender(update.getMessage().getFrom());
//            check if it is quiz related (reply for force reply) message
            if (update.getMessage().getReply_to_message()!= null){
                if (quizService.getQuizByCredentials(update.getMessage().getReply_to_message().getMessage_id(),msgManager.getOriginalSender().getId())!= null){
                    quizService.resolveQuizAnswer(update);
                }
            }
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
                            msgManager.sendTelegramObjAsMessage(new SendMessage()
                                    .setText("Inline message")
                                    .setReply_markup(new InlineKeyboardMarkup.KeyboardBuilder(3, 1, names).build())
                                    .setChat_id(msgManager.getOriginalSender().getId()));
                        }
                        case "/quiz" -> {
                            if (checkCommandLenght(2, "/quiz")) {
                                if (commands[1].equalsIgnoreCase("me")) quizService.sendQuizToId(msgManager.getME());
                                if (commands[1].equalsIgnoreCase("yas")) quizService.sendQuizToYasia();
                                if (commands[1].equalsIgnoreCase("chom"))
                                    quizService.sendQuizToId(msgManager.getCHOMIK());
                                if (commands[1].equalsIgnoreCase("id")){
                                    if(checkCommandLenght(3,"/quiz id"))
                                        quizService.sendQuizToId(Long.decode(commands[2]));
                                }
                            }
                        }
                        case "/hmql" -> msgManager.sendBackTelegramTextMessage("Zostało "+quizRepository.findAllQuizEntitiesToSend().size()+" pytań.");
                        case "/smk" -> {
                            String[] names = {"Reply", "she", "goes"};
                            msgManager.sendTelegramObjAsMessage(new SendMessage()
                                    .setText("Message with keyboard")
                                    .setReply_markup(new ReplyKeyboardMarkup.KeyboardBuilder(3, 1, names).build())
                                    .setChat_id(msgManager.getOriginalSender().getId()));
                        }
                        case "/rmk" -> {
                            msgManager.sendTelegramObjAsMessage(new SendMessage()
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
                                        hueAuthorizationService.sendAuthorizationLink(commands[2]);
                                }
                                if (commands[1].equalsIgnoreCase("checktokens")) {
//
//
//                                    TODO
//
//
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
    }

    @GetMapping("/hue/code")
    public void hueAuthentication(@RequestParam String code, @RequestParam String state){
        log.debug("New Hue code retrieved!"+code);
        hueAuthorizationService.authenticateApp(state,code);
        log.debug("End of RequestHandler.hueAuthentication<<");
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
}
