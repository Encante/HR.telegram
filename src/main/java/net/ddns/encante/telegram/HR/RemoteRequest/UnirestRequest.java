package net.ddns.encante.telegram.HR.RemoteRequest;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import net.ddns.encante.telegram.HR.Messages.SentMessage;
import net.ddns.encante.telegram.HR.SendMessage;
import net.ddns.encante.telegram.HR.SentMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnirestRequest implements RemoteRequest{
    private final String BOT_TOKEN = "<BOT_TOKEN>";
    private final String SEND_MESSAGE_URL = "https://api.telegram.org/bot"+ BOT_TOKEN +"/sendMessage";
    private final String EDIT_MESSAGE_REPLY_MARKUP_URL = "https://api.telegram.org/bot"+ BOT_TOKEN +"/editMessageReplyMarkup";
    @Autowired
    final Gson gson;

    public SentMessage sendMessageObject(SendMessage message){
    HttpResponse<JsonNode> response = Unirest.post(SEND_MESSAGE_URL)
            .header("Content-Type", "application/json")
            .body(gson.toJson(message))
            .asJson();
    System.out.println("BODY SENT: "+gson.toJson(message));
    printResponseToConsole(response);
    SentMessage m =new SentMessage();
    return m;kla
//    gson.fromJson(response.getBody().toString(), SentMessage.class);
}

public HttpResponse<JsonNode> editMessageObject(Object message){
            HttpResponse<JsonNode> response = Unirest.post(EDIT_MESSAGE_REPLY_MARKUP_URL)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(message))
                    .asJson();
            System.out.println("BODY SENT: "+gson.toJson(message));
            printResponseToConsole(response);
            return response;
}


private void printResponseToConsole(HttpResponse<JsonNode> response){
    System.out.println("RESPONSE STATUS: \r\n" + response.getStatus()
            + " "
            + response.getStatusText()
            + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
            + "\r\nBODY: " + response.getBody().toPrettyString());
}

}
