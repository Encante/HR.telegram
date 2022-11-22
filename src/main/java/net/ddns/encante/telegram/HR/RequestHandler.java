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
//      check if have any text
        if (update.message.getText()!= null) {
//        check if any and if there is do commands:
            if (update.message.getText().charAt(0) == '/') {
                String[] commands = update.message.getText().split(" ");
                switch (commands[0]) {
                    case "/hi" -> request.sendMessageToChatId("Hello " + update.message.getFrom().getFirst_name() + "!", update.message.getChat().getId());
                    case "/sm" -> {
                        if (commands[1].equalsIgnoreCase("m"))
                            request.sendMessageToChatId(update.message.getText().substring(6), 5580797031L);
                        if (commands[1].equalsIgnoreCase("y"))
                            request.sendMessageToChatId(update.message.getText().substring(6), 566760042L);
                    }
//                    case "/test" -> {
//                        request.
//                    }
                }
            }
//        if not from me, send message to me
            if (update.message.getFrom().getId() != 5580797031L) {
                update.sendUpdateToChatId(request,5580797031L);
            }

//        then print to console
            update.printUpdateToConsole();
            return "ok";
        }
        else{
//            if no text send me an info
            update.sendUpdateToChatId(request,5580797031L);
//            and print to console
            update.printUpdateToConsole();
            return "ok";
        }
    }

}
