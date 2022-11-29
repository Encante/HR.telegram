package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
        SendMessage sendMessage = new SendMessage();
//        do WebhookUpdate object from JSON
        System.out.println(content);
        WebhookUpdate update = gson.fromJson(content, WebhookUpdate.class);
//            check if it is callback
        if (update.getCallback_query()!=null){
            sendMessage.setChat_id(5580797031L);
            sendMessage.setText("Callback received! T: "
                    + Utils.getCurrentDateTime()
                    + "FROM: "
                    + update.callback_query.getFrom().getFirst_name()
                    +" "
                    + update.callback_query.getFrom().getLast_name()
                    + " CALLBACK DATA: "
                    +update.callback_query.getData());
            sendMessage.sendMessageToChatIdByObject(ReplyKeyboardType.NO);
        }
//      check if have any message
        if (update.message!=null) {
//      check if incoming message have any text
            if (update.message.getText() != null) {
//        check if incoming message have any and if there is do commands:
                if (update.message.getText().charAt(0) == '/') {
                    String[] commands = update.message.getText().split(" ");

                    switch (commands[0]) {
                        case "/hi" -> {
                            sendMessage.setChat_id(update.message.getFrom().getId());
                            sendMessage.setText("Hello " + update.message.getFrom().getFirst_name() + "!");
                            sendMessage.sendMessageToChatIdByObject(ReplyKeyboardType.NO);
                        }
                        case "/sm" -> {
                            //dopisac ochrone przed pustym 2 parametrem
                            if (commands[1].equalsIgnoreCase("m")){
                                sendMessage.setChat_id(5580797031L);
                                sendMessage.setText(update.message.getText().substring(6));
                                sendMessage.sendMessageToChatIdByObject(ReplyKeyboardType.NO);
                            }
                            if (commands[1].equalsIgnoreCase("y")) {
                                sendMessage.setChat_id(566760042L);
                                sendMessage.setText(update.message.getText().substring(6));
                                sendMessage.sendMessageToChatIdByObject(ReplyKeyboardType.NO);
                            }
                        }
                    }
                }

//        if not from me, send message to me
                if (update.message.getFrom().getId() != 5580797031L) {
                    sendMessage.setChat_id(566760042L);
                    sendMessage.setText(update.message.getText());
                    sendMessage.sendMessageToChatIdByObject(ReplyKeyboardType.NO);
                }
//        then print to console
                update.printUpdateToConsole();
                return "ok";
            } else {
//            if no text send me an info
                sendMessage.sendUpdateToChatId(update, 5580797031L);
//            and print to console
                update.printUpdateToConsole();
                return "ok";
            }
        }
        return "nok";
    }

}
