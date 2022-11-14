package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RequestHandler {
//    get beans ;)
    @Autowired
    RemoteRequest request;
    @Autowired
    Gson gson;
//    when receiving message:
    @PostMapping("/HR4telegram")
    public String postHandler (@RequestBody String content){
//        do WebhookUpdate object from JSON
        WebhookUpdate update = gson.fromJson(content, WebhookUpdate.class);
//        check for and do commands:
        if (update.message.getText().charAt(0) == '/') {
            String[] commands = update.message.getText().split(" ");
            switch (commands[0]) {
                case "/hi" -> request.sendMessageToChatId("Hello " + update.message.from.getFirst_name() + "!", update.message.chat.getId());
                case "/sm" -> {
                    if (commands[1].equalsIgnoreCase("m"))
                        request.sendMessageToChatId(update.message.getText().substring(6), 5580797031L);
                    if (commands[1].equalsIgnoreCase("y"))
                        request.sendMessageToChatId(update.message.getText().substring(6), 566760042L);
                }
            }
        }
//        if not from me, send message to me
if (update.message.from.getId() != 5580797031L)
        {
            request.sendMessageToChatId("New message! T: " + Utils.getCurrentDateTime() + " FROM: " + update.message.from.getFirst_name() + " " + update.message.from.getLast_name() + " CHAT ID: " + update.message.chat.getId() + " CONTENT: " + update.message.getText(), 5580797031L);
        }

//        then print to console
        System.out.println("update id: " +update.getUpdate_id());
        System.out.println("chat id: " +update.message.chat.getId());
        System.out.println("message id: " + update.message.getMessage_id());
        System.out.println("message: " + update.message.getText());
        System.out.println("From: " + update.message.from.getFirst_name());
        if (update.message.from.getLast_name() != null)
        System.out.println(update.message.from.getLast_name());
        System.out.println("Is bot: " + update.message.from.is_bot());
        return "ok";
    }

}
