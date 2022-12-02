package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RequestHandler {
//    get beans ;)

    //    when receiving message:
    @PostMapping("/HR4telegram")
    public String postHandler(@RequestBody String content) {
        Gson gson = new Gson();
//        do WebhookUpdate object from JSON
        System.out.println(content);
        WebhookUpdate update = gson.fromJson(content, WebhookUpdate.class);
//            check if it is callback
        if (update.getCallback_query() != null) {
            new EditMessageReplyMarkup(update.callback_query)
                    .edit();
            SendMessage.builder()
                    .chat_id(5580797031L)
                    .text("Callback received! T: "
                            + Utils.getCurrentDateTime()
                            + "FROM: "
                            + update.callback_query.getFrom().getFirst_name()
                            + " "
                            + update.callback_query.getFrom().getLast_name()
                            + " CALLBACK DATA: "
                            + update.callback_query.getData())
                    .build()
                    .sendMessageWithKeyboard(ReplyKeyboardType.NO);
        }
//      check if have any message
        if (update.message != null) {
//      check if incoming message have any text
            if (update.getMessage().getText() != null) {
//        check if incoming message have any and if there is do commands:
                if (update.getMessage().getText().charAt(0) == '/') {
                    String[] commands = update.message.getText().split(" ");
                    switch (commands[0]) {
                        case "/hi" -> SendMessage.builder()
                                .chat_id(update.message.getFrom().getId())
                                .text("Hello " + update.message.getFrom().getFirst_name() + "!")
                                .build().sendMessageWithKeyboard(ReplyKeyboardType.NO);
                        case "/sm" -> {
                            if (commands.length < 3) {//command content validation
                                SendMessage.builder()
                                        .text("WARNING! BAD COMMAND!")
                                        .build()
                                        .sendToMe();
                            } else {
                                if (commands[1].equalsIgnoreCase("m")) {
                                    SendMessage.builder()
                                            .chat_id(5580797031L)
                                            .text(update.message.getText().substring(6))
                                            .build().sendMessageWithKeyboard(ReplyKeyboardType.NO);
                                }
                                if (commands[1].equalsIgnoreCase("y")) {
                                    SendMessage.builder()
                                            .chat_id(566760042L)
                                            .text(update.message.getText().substring(6))
                                            .build().sendMessageWithKeyboard(ReplyKeyboardType.NO);
                                }
                            }
                        }
                        case "/smi" -> {
                            String[] names = {"Inline", "she", "goes"};
                            SentMessage inline = SendMessage.builder().chat_id(5580797031L)
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
                                EditMessage edit = new EditMessageReplyMarkup(sent).edit();
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
                if (update.message.getFrom().getId() != 5580797031L) {
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