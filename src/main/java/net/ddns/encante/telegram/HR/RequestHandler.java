package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import net.ddns.encante.telegram.HR.Messages.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.Messages.WebhookUpdate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RequestHandler {

//        when receiving message:
    @PostMapping("/HR4telegram")
    public String postHandler(@RequestBody String content) {
        Gson gson = new Gson();
//        do WebhookUpdate object from JSON
        System.out.println(content);
        WebhookUpdate update = gson.fromJson(content, WebhookUpdate.class);
//            check if it is callback
        if (update.getCallback_query() != null) {
//            delete keyboard after pressing a key
            new EditMessageReplyMarkup(update.callback_query)
                    .edit();
//            send me a message with callback
            SendMessage.builder()
                    .text("Callback received! T: "
                            + Utils.getCurrentDateTime()
                            + "FROM: "
                            + update.getCallback_query().getFrom().getFirst_name()
                            + " "
                            + update.getCallback_query().getFrom().getLast_name()
                            + " CALLBACK DATA: "
                            + update.getCallback_query().getData())
                    .build()
                    .sendToMe();
        }
//      check if have any message
        if (update.getMessage() != null) {
//      check if incoming message have any text
            if (update.getMessage().getText() != null) {
//        check if incoming message have any and if there is do commands:
                if (update.getMessage().getText().charAt(0) == '/') {
                    String[] commands = update.getMessage().getText().split(" ");
                    switch (commands[0]) {
                        case "/hi" -> SendMessage.builder()
                                .chat_id(update.getMessage().getFrom().getId())
                                .text("Hello " + update.getMessage().getFrom().getFirst_name() + "!")
                                .build()
                                .send();
                        case "/sm" -> {
                            if (commands.length < 3) {//command content validation
                                SendMessage.builder()
                                        .text("WARNING! BAD COMMAND!")
                                        .build()
                                        .sendToMe();
                            } else {
                                if (commands[1].equalsIgnoreCase("m")) {
                                    SendMessage.builder()
                                            .text(update.getMessage().getText().substring(6))
                                            .build()
                                            .sendToMe();
                                }
                                if (commands[1].equalsIgnoreCase("y")) {
                                    SendMessage.builder()
                                            .chat_id(566760042L)
                                            .text(update.getMessage().getText().substring(6))
                                            .build()
                                            .send();
                                }
                            }
                        }
                        case "/smi" -> {
                            String[] names = {"Inline", "she", "goes"};
                            SendMessage.builder().chat_id(5580797031L)
                                    .text("Inline message")
                                    .reply_markup(new InlineKeyboardMarkup.KeyboardBuilder(3, 1, names).build())
                                    .build().send();
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
                                new EditMessageReplyMarkup(sent).edit();
                            } else {
                                SendMessage.builder()
                                        .text("WARNING! BAD COMMAND!")
                                        .build()
                                        .sendToMe();
                            }
                        }
                    }
                }
                //        if not from me, send message to me
                if (update.getMessage().getFrom().getId() != 5580797031L) {
                    SendMessage.builder()
                            .text("New message! T: " + Utils.getCurrentDateTime()
                                    + "  FROM: "
                                    + update.getMessage().getFrom().getFirst_name()
                                    + " "
                                    + update.getMessage().getFrom().getLast_name()
                                    + "  CHAT ID: "
                                    + update.getMessage().getChat().getId()
                                    + "  CONTENT: "
                                    + update.getMessage().getText())
                            .build()
                            .sendToMe();
                }
//        then print to console
                update.printUpdateToConsole();
                return "ok";
            }
            else {
//            if no text send me an info
                SendMessage.builder().text("New message! T: " + Utils.getCurrentDateTime()
                        + "  FROM: "
                        + update.getMessage().getFrom().getFirst_name()
                        + " "
                        + update.getMessage().getFrom().getLast_name()
                        + "  CHAT ID: "
                        + update.getMessage().getChat().getId()
                        + " But it has no text!")
                        .build()
                        .sendToMe();
//            and print to console
                update.printUpdateToConsole();
                return "ok";
            }
        }
        else return "nok";
    }
}