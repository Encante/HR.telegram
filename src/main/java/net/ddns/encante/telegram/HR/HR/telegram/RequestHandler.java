package net.ddns.encante.telegram.HR.HR.telegram;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestHandler {

//    ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping("/HR4telegram")
    public String postHandler (@RequestBody String content){
        Gson gson = new Gson();
        WebhookUpdate update = gson.fromJson(content, WebhookUpdate.class);
//        send message to me
        RemoteRequest request = new RemoteRequest();
        request.sendMessage("New message! T: "+Utils.getCurrentDateTime()+" FROM: "+update.message.from.getFirst_name()+" CHAT ID: "+update.message.chat.getId()+" CONTENT: "+update.message.getText());

//        then print to console
        System.out.println("update id: " +update.getUpdate_id());
        System.out.println("chat id: " +update.message.chat.getId());
        System.out.println("message id: " + update.message.getMessage_id());
        System.out.println("message: " + update.message.getText());
        System.out.println("From: " + update.message.from.getFirst_name());
        System.out.println("Is bot: " + update.message.from.is_bot());
        return "ok";
    }

}
