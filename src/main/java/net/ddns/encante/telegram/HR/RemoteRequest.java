package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.springframework.stereotype.Component;

@Component
public class RemoteRequest {
    private final String BOT_TOKEN = "<BOT_TOKEN>";
    private final String SEND_MESSAGE_URL = "https://api.telegram.org/bot"+ BOT_TOKEN +"/sendMessage";
    private final String EDIT_MESSAGE_REPLY_MARKUP_URL = "https://api.telegram.org/bot"+ BOT_TOKEN +"/editMessageReplyMarkup";
    private final Gson gson = new Gson();
public HttpResponse<JsonNode> sendMessageAsJson(String body){
    HttpResponse<JsonNode> response = Unirest.post(SEND_MESSAGE_URL)
            .header("Content-Type", "application/json")
            .body(body)
            .asJson();
    System.out.println("BODY SENT: "+body);
    printResponseJsonToConsole(response);
    return response;
}

public HttpResponse<JsonNode> editMessageAsObject(Object message){
            HttpResponse<JsonNode> response = Unirest.post(EDIT_MESSAGE_REPLY_MARKUP_URL)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(message))
                    .asJson();
            System.out.println("BODY SENT: "+gson.toJson(message));
            printResponseJsonToConsole(response);
            return response;
}


private void printResponseJsonToConsole(HttpResponse<JsonNode> response){
    System.out.println("RESPONSE STATUS: \r\n" + response.getStatus()
            + " "
            + response.getStatusText()
            + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
            + "\r\nBODY: " + response.getBody().toPrettyString());
}

}
