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
    public String postHandler (@RequestBody String content){
        Gson gson = new Gson();
//        do WebhookUpdate object from JSON
        System.out.println(content);
        WebhookUpdate update = gson.fromJson(content, WebhookUpdate.class);
//            check if it is callback
        if (update.getCallback_query()!=null){
            SendMessage.builder()
                    .chat_id(5580797031L)
                    .text("Callback received! T: "
                            + Utils.getCurrentDateTime()
                            + "FROM: "
                            + update.callback_query.getFrom().getFirst_name()
                            +" "
                            + update.callback_query.getFrom().getLast_name()
                            + " CALLBACK DATA: "
                            +update.callback_query.getData())
                            .build()
                                    .sendMessageWithKeyboard(ReplyKeyboardType.NO);
        }
//      check if have any message
        if (update.message!=null) {
//      check if incoming message have any text
            if (update.message.getText() != null) {
//        check if incoming message have any and if there is do commands:
                if (update.message.getText().charAt(0) == '/') {
                    String[] commands = update.message.getText().split(" ");

                    switch (commands[0]) {
                        case "/hi" ->
                            SendMessage.builder()
                                    .chat_id(update.message.getFrom().getId())
                                            .text("Hello " + update.message.getFrom().getFirst_name() + "!")
                                                    .build().sendMessageWithKeyboard(ReplyKeyboardType.NO);
                        case "/sm" -> {
                            if (commands.length<3){//command content validation
                                SendMessage.builder()
                                        .chat_id(5580797031L)
                                        .text("Bad command!")
                                        .build().sendMessageWithKeyboard(ReplyKeyboardType.NO);
                                System.out.println("WARNING! BAD COMMAND!");
                            }
                            else {
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
                    }
                }

//        if not from me, send message to me
                if (update.message.getFrom().getId() != 5580797031L) {
                    SendMessage.builder()
                            .chat_id(5580797031L)
                            .text(update.message.getText())
                            .build().send();
                }
//        then print to console
                update.printUpdateToConsole();
                return "ok";
            } else {
//            if no text send me an info
                SendMessage.builder().build().sendTextUpdateToChatId(update,5580797031L);
//            and print to console
                update.printUpdateToConsole();
                return "ok";
            }
        }
        return "nok";
    }

}
