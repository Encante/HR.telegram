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
        System.out.println(content);
        WebhookUpdate update = gson.fromJson(content, WebhookUpdate.class);
//            check if it is callback
        if (update.getCallback_query()!=null){
//            switch (update.callback_query.getData()){
//                case "a" -> {
//                    request.sendMessageToChatId("wybrałeś A", 5580797031L);
//                }
//                case "b" -> {
//                    request.sendMessageToChatId("wybrałeś B", 5580797031L);
//                }
//                case "c" -> {
//                    request.sendMessageToChatId("wybrałeś C", 5580797031L);
//                }
//            }
            request.sendMessageToChatId("Callback received! T: "
                    + Utils.getCurrentDateTime()
                    + "FROM: "
                    + update.callback_query.getFrom().getFirst_name()
                    +" "
                    + update.callback_query.getFrom().getLast_name()
                    + " CALLBACK DATA: "
                    +update.callback_query.getData()
                    ,5580797031L);
        }
//      check if have any message
        if (update.message!=null) {
//      check if incoming message have any text
            if (update.message.getText() != null) {
//        check if incoming message have any and if there is do commands:
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
                        case "/inline" -> {
                            if (commands[1].equalsIgnoreCase("m"))
                                request.testKochana(5580797031L);
                            if (commands[1].equalsIgnoreCase("y"))
                                request.testKochana(566760042L);
                        }
//                    case "/test" -> {
//                        request.
//                    }
                    }
                }
//        if not from me, send message to me
                if (update.message.getFrom().getId() != 5580797031L) {
                    update.sendUpdateToChatId(request, 5580797031L);
                }

//        then print to console
                update.printUpdateToConsole();
                return "ok";
            } else {
//            if no text send me an info
                update.sendUpdateToChatId(request, 5580797031L);
//            and print to console
                update.printUpdateToConsole();
                return "ok";
            }
        }
        return "nok";
    }

}
