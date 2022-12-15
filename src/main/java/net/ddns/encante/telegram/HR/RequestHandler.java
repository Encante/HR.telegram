package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import net.ddns.encante.telegram.HR.RemoteRequest.RemoteRequest;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessageReplyMarkup;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestHandler {
@Autowired
Gson gson;
@Autowired
RemoteRequest request;

//        when receiving message:
    @PostMapping("/HR4telegram")
    public String postHandler(@RequestBody String content) {
//        do WebhookUpdate object from JSON
        System.out.println("CONTENT OF A WEBHOOK UPDATE BODY:");
        System.out.println(content);
        WebhookUpdate update = gson.fromJson(content, WebhookUpdate.class);
//            check if it is callback
        if (update.getCallback_query() != null) {
//            delete keyboard after pressing a key
            request.editTelegramMessage(new EditMessage(update.getCallback_query()));
//            send me a message with callback
            request.sendTelegramMessage(new SendMessage()
                    .setText("Callback received! T: "
                            + Utils.getCurrentDateTime()
                            + "FROM: "
                            + update.getCallback_query().getFrom().getFirst_name()
                            + " "
                            + update.getCallback_query().getFrom().getLast_name()
                            + " CALLBACK DATA: "
                            + update.getCallback_query().getData())
                    .toMe());
        }
//      check if have any message
        if (update.getMessage() != null) {
//      check if incoming message have any text
            if (update.getMessage().getText() != null) {
//        check if incoming message have any and if there is do commands:
                if (update.getMessage().getText().charAt(0) == '/') {
                    String[] commands = update.getMessage().getText().split(" ");
                    switch (commands[0]) {
                        case "/hi" -> request.sendTelegramMessage(new SendMessage()
                                .setChat_id(update.getMessage().getFrom().getId())
                                .setText("Hello " + update.getMessage().getFrom().getFirst_name() + "!"));
                        case "/sm" -> {
                            if (commands.length < 3) {//command content validation
                                request.sendTelegramMessage(new SendMessage()
                                        .setText("WARNING! BAD COMMAND!")
                                        .toMe());
                            } else {
                                if (commands[1].equalsIgnoreCase("m")) {
                                    request.sendTelegramMessage(new SendMessage()
                                            .setText(update.getMessage().getText().substring(6))
                                            .toMe());
                                }
                                if (commands[1].equalsIgnoreCase("y")) {
                                    request.sendTelegramMessage(new SendMessage()
                                            .setChat_id(566760042L)
                                            .setText(update.getMessage().getText().substring(6)));
                                }
                            }
                        }
                        case "/smi" -> {
                            String[] names = {"Inline", "she", "goes"};
                            request.sendTelegramMessage(new SendMessage()
                                    .setText("Inline message")
                                    .setReply_markup(new InlineKeyboardMarkup.KeyboardBuilder(3, 1, names).build())
                                    .toMe());
                        }
                        case "/rmk" -> {
                            if (commands.length > 1) {
                                SentMessage sent = new SentMessage();
                                Chat chat = new Chat();
                                chat.setId(5580797031L);
                                Message msg = new Message();
                                msg.setMessage_id(Long.parseLong(commands[1]));
                                msg.setChat(chat);
                                sent.setResult(msg);
                                request.editTelegramMessage(new EditMessageReplyMarkup(sent));
                            } else {
                                request.sendTelegramMessage(new SendMessage()
                                        .setText("WARNING! BAD COMMAND!")
                                        .toMe());
                            }
                        }
                    }
                }
                //        if not from me, send message to me
                if (update.getMessage().getFrom().getId() != 5580797031L) {
                    request.sendTelegramMessage(new SendMessage()
                            .setText("New message! T: " + Utils.getCurrentDateTime()
                                    + "  FROM: "
                                    + update.getMessage().getFrom().getFirst_name()
                                    + " "
                                    + update.getMessage().getFrom().getLast_name()
                                    + "  CHAT ID: "
                                    + update.getMessage().getChat().getId()
                                    + "  CONTENT: "
                                    + update.getMessage().getText())
                            .toMe());
                }
//        then print to console
                update.printUpdateToConsole();
                return "200";
            }
            else {
//            if no text send me an info
                request.sendTelegramMessage(new SendMessage()
                .setText("New message! T: " + Utils.getCurrentDateTime()
                        + "  FROM: "
                        + update.getMessage().getFrom().getFirst_name()
                        + " "
                        + update.getMessage().getFrom().getLast_name()
                        + "  CHAT ID: "
                        + update.getMessage().getChat().getId()
                        + " But it has no text!")
                        .toMe());
//            and print to console
                update.printUpdateToConsole();
                return "200";
            }
        }
        else return "nok";
    }
}