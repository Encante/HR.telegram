package net.ddns.encante.telegram.HR.RemoteRequest;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.SentMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnirestRequest implements RemoteRequest{
    private final String BOT_TOKEN = "xxx";
    private final String API_URL = "https://api.telegram.org/bot"+ BOT_TOKEN;
    private final String SEND_MESSAGE_URL = "https://api.telegram.org/bot"+ BOT_TOKEN +"/sendMessage";
    private final String EDIT_MESSAGE_REPLY_MARKUP_URL = "https://api.telegram.org/bot"+ BOT_TOKEN +"/editMessageReplyMarkup";
    private HttpResponse<JsonNode> response;
    @Autowired
    Gson gson;

    public void answerCallbackQuery(AnswerCallbackQuery answer){
        this.response = Unirest.post(API_URL+"/answerCallbackQuery")
                .header("Content-Type", "application/json")
                .body(gson.toJson(answer))
                .asJson();
        System.out.println("BODY SENT BY answerCallbackQuery : "+gson.toJson(answer));
        printResponseToConsole();
    }
    public SentMessage sendTelegramMessage(SendMessage message){
    this.response = Unirest.post(SEND_MESSAGE_URL)
            .header("Content-Type", "application/json")
            .body(gson.toJson(message))
            .asJson();
    System.out.println("BODY SENT BY sendMessageObject: "+gson.toJson(message));
    printResponseToConsole();
    return gson.fromJson(response.getBody().toString(),SentMessage.class);
}
    public SentMessage editTelegramMessage(EditMessage message){
            this.response = Unirest.post(EDIT_MESSAGE_REPLY_MARKUP_URL)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(message))
                    .asJson();
            System.out.println("BODY SENT by editMessageObject: "+gson.toJson(message));
            printResponseToConsole();
        return gson.fromJson(response.getBody().toString(),SentMessage.class);
}
public void printResponseToConsole(){
    System.out.println("RESPONSE STATUS: \r\n" + response.getStatus()
            + " "
            + response.getStatusText()
            + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
            + "\r\nBODY: " + response.getBody().toPrettyString());
}
}
