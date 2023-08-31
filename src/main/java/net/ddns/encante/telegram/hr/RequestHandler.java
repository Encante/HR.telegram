package net.ddns.encante.telegram.hr;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.ddns.encante.telegram.hr.hue.entity.HueAuthorization;
import net.ddns.encante.telegram.hr.hue.service.HueAuthorizationService;
import net.ddns.encante.telegram.hr.menu.service.MenuService;
import net.ddns.encante.telegram.hr.quiz.service.QuizService;
import net.ddns.encante.telegram.hr.telegram.api.methods.AnswerCallbackQuery;
import net.ddns.encante.telegram.hr.telegram.api.methods.SendMessage;
import net.ddns.encante.telegram.hr.telegram.api.objects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.hr.telegram.api.objects.ReplyKeyboardMarkup;
import net.ddns.encante.telegram.hr.telegram.api.objects.ReplyKeyboardRemove;
import net.ddns.encante.telegram.hr.telegram.api.objects.WebhookUpdate;
import net.ddns.encante.telegram.hr.telegram.service.MessageManager;
import net.ddns.encante.telegram.hr.telegram.service.WebhookUpdateService;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
public class RequestHandler {
private Gson gson;
private WebhookUpdateService webhookService;
private QuizService quizService;
private HueAuthorizationService hueAuthorizationService;
private MenuService menuService;
private MessageManager msgManager;
private String[] commands;

public RequestHandler(Gson gson, WebhookUpdateService wus, QuizService qs, HueAuthorizationService has, MenuService ms, MessageManager mgr){
    this.gson=gson;
    this.webhookService=wus;
    this.quizService = qs;
    this.hueAuthorizationService=has;
    this.menuService=ms;
    this.msgManager=mgr;
}
    //        when receiving message:
    @PostMapping("/HR4telegram")
    public void postHandler(@RequestBody WebhookUpdate update) {// do WebhookUpdate object from JSON
//        log incoming update
        log.debug("INCOMING WEBHOOK UPDATE BODY:");
//        log.debug(gson.toJson(JsonParser.parseString(content)));
        log.debug(gson.toJson(update));
//        store update in the DB
        webhookService.saveWebhookUpdate(update);
//            check if it is callback
        if (update.getCallback_query() != null) {
            //        initialise msgManager setting originalSender of message
            msgManager.setOriginalSender(update.getCallback_query().getFrom());
//            check if it is a quiz message callback and eventually resolve a quiz
            if(quizService.getQuizByCredentials(msgManager.getOriginalSender().getId(), update.getCallback_query().getMessage().getMessage_id())!=null){
//              actually resolve answer
                quizService.resolveQuizAnswer(update);
                log.info("quiz callback resolved.");
            }
//            check if it is a menu callback and eventually handle it
            else if(menuService.getMenuByCredentials(msgManager.getOriginalSender().getId(), update.getCallback_query().getMessage().getMessage_id())!=null){
                menuService.handleMenuCallback(update);
                log.info("Menu callback resolved");
            }
//                nothing specific other than quiz is designed to be used by callbacks so now default behavior will be sending "Callback Answer" and deleting an inline keyboard
            else {
//            answer callback query
                msgManager.answerCallbackQuery(new AnswerCallbackQuery(update.getCallback_query().getId(), "Callback Answer!", false));
//            delete keyboard after pressing a key
                msgManager.editTelegramMessageText(update.getCallback_query().getFrom().getId(),update.getCallback_query().getMessage().getMessage_id(),update.getCallback_query().getMessage().getText());
            }
//      if it doesnt have callback querry, check if it has any message obj
        } else if (update.getMessage() != null) {
//        just little helpful var with chatID of who send msg
            msgManager.setOriginalSender(update.getMessage().getFrom());
//                check if it is reply to other msg
            if (update.getMessage().getReply_to_message()!= null){
//            check if it is quiz related (reply for force reply) message
                if (quizService.getQuizByCredentials(msgManager.getOriginalSender().getId(), update.getMessage().getReply_to_message().getMessage_id())!= null){
                    quizService.resolveQuizAnswer(update);
                    log.info("quiz answer resolved.");
//                    if it is not quiz related reply to bot message and it is not from me send message to me
                }else if (menuService.getMenuByCredentials(msgManager.getOriginalSender().getId(), update.getMessage().getReply_to_message().getMessage_id())!=null){
                    menuService.handleMenuReply(update);
                    log.info("Menu answer resolved.");
                } else if(!msgManager.getOriginalSender().getId().equals(msgManager.getME())){
                    msgManager.sendTelegramTextMessage("New reply! T: " + Utils.getCurrentDateTime()
                            + "  FROM: "
                            + msgManager.getOriginalSender().getFirst_name()
                            + " "
                            + update.getMessage().getFrom().getLast_name()
                            + "  CHAT ID: "
                            + msgManager.getOriginalSender().getId()
                            + "  CONTENT: "
                            + update.getMessage().getText(), msgManager.getME());
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
                        case "/menu" -> menuService.createMainMenu(msgManager.getOriginalSender().getId());
                        case "/start" -> msgManager.greetFirstTime();
//                        send message through bot
                        case "/sm" -> {
                            if (checkCommandLenght(3, "/sm")) {
//                                send message to me - checking purposes
                                if (commands[1].equalsIgnoreCase("m")) {
                                    msgManager.sendTelegramTextMessage(update.getMessage().getText().substring(6), msgManager.getME());
                                }
//                                send message to Yas
                                if (commands[1].equalsIgnoreCase("y")) {
                                    msgManager.sendTelegramTextMessage(update.getMessage().getText().substring(6), msgManager.getYASIA());
                                }
//                                send msg to chom
                                if (commands[1].equalsIgnoreCase("c")) {
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
                                if (commands[1].equalsIgnoreCase("me"))
                                    quizService.sendQuizToId(msgManager.getME());
                                if (commands[1].equalsIgnoreCase("yas")) quizService.sendQuizToYasia();
                                if (commands[1].equalsIgnoreCase("chom"))
                                    quizService.sendQuizToId(msgManager.getCHOMIK());
                                if (commands[1].equalsIgnoreCase("id")) {
                                    if (checkCommandLenght(3, "/quiz id"))
                                        quizService.sendQuizToId(Long.decode(commands[2]));
                                }
                            }
                        }
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
                            if (checkCommandLenght(2, "/searchUserById")) {
                                if (webhookService.getUserEntityByUserId(Long.decode(commands[1])) != null) {
                                    msgManager.sendBackTelegramTextMessage("To " + webhookService.getUserEntityByUserId(Long.decode(commands[1])).getFirstName());
                                } else
                                    msgManager.sendBackTelegramTextMessage("User with id " + commands[1] + " not in DB!");
                            }
                        }
                        case "/hueapp" -> {
//                            command lenght check
                            if (checkCommandLenght(3, "/hueapp")) {
                                if (commands[1].equalsIgnoreCase("link")) {
                                    hueAuthorizationService.sendAuthorizationLink(commands[2]);
                                }
                                if (commands[1].equalsIgnoreCase("add")) {
                                    if (checkCommandLenght(5, "/hueapp add")) {
                                        HueAuthorization authorization = new HueAuthorization();
                                        authorization.setClientId(commands[2]);
                                        authorization.setClientSecret(commands[3]);
                                        authorization.setDisplayName(commands[4]);
                                        hueAuthorizationService.saveOrUpdateAuthorizationBasedOnClientId(authorization);
                                        msgManager.sendBackTelegramTextMessage("hue App " + authorization.getDisplayName() + " added to DB.");
                                    }
                                }
                                if (commands[1].equalsIgnoreCase("searchByName")) {
                                    if (hueAuthorizationService.getAuthorizationForDisplayName(commands[2]) != null) {
                                        HueAuthorization entity =
                                                hueAuthorizationService.getAuthorizationForDisplayName(commands[2]);
                                        msgManager.sendBackTelegramTextMessage("App with this Id already in DB with client ID: " + entity.getClientId());
                                    } else msgManager.sendBackTelegramTextMessage("No app with this name added");
                                }
                            }
                        }
                    }
                }
                //        if not from me, send message to me
                if (!msgManager.getOriginalSender().getId().equals(msgManager.getME())) {
                    msgManager.sendTelegramTextMessage("New message T: " + Utils.getCurrentDateTime()
                            + "  FROM: "
                            + msgManager.getOriginalSender().getFirst_name()
                            + " "
                            + update.getMessage().getFrom().getLast_name()
                            + "  CHAT ID: "
                            + msgManager.getOriginalSender().getId()
                            + "  CONTENT: "
                            + update.getMessage().getText(), msgManager.getME());
                }
//                if message has no text and is not from me send me an info
            } else {

                if (!msgManager.getOriginalSender().getId().equals(msgManager.getME())) {
                    msgManager.sendTelegramTextMessage("New message T: " + Utils.getCurrentDateTime()
                            + "  FROM: "
                            + msgManager.getOriginalSender().getFirst_name()
                            + " "
                            + msgManager.getOriginalSender().getLast_name()
                            + "  CHAT ID: "
                            + msgManager.getOriginalSender().getId()
                            + " But it has no text.", msgManager.getME());
                }
            }
            //        update not contains message object and is not a callback and not from me - send me an info
        }else {
            if (!msgManager.getOriginalSender().getId().equals(msgManager.getME())){
                msgManager.sendTelegramTextMessage("New message. T: " + Utils.getCurrentDateTime()
                        + "  FROM: "
                        + msgManager.getOriginalSender().getFirst_name()
                        + " "
                        + msgManager.getOriginalSender().getLast_name()
                        + "  CHAT ID: "
                        + msgManager.getOriginalSender().getId()
                        + " But it has no message object nor is a callback.", msgManager.getME());
            }
        }
    }

    @GetMapping("/hue/code")
    public void hueAuthentication(@RequestParam String code, @RequestParam String state){
        log.debug("New hue code retrieved!"+code);
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
            msgManager.sendAndLogErrorMsg("RH.cCL","Bad command lenght - insuficient parameters from command "+ invoker);
            return false;
        }
    }
}
