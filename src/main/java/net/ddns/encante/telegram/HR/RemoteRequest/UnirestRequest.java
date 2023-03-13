package net.ddns.encante.telegram.HR.RemoteRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import net.ddns.encante.telegram.HR.Hue.HueAuthorization;
import net.ddns.encante.telegram.HR.Hue.HueTokens;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.SentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UnirestRequest implements RemoteRequest{
    private static final Logger log = LoggerFactory.getLogger("net.ddns.encante.telegram.HR.RemoteRequest");
    private final String BOT_TOKEN = "XXX";
    private final String API_URL = "https://api.telegram.org/bot"+ BOT_TOKEN;
    private final String SEND_MESSAGE_URL = "https://api.telegram.org/bot"+ BOT_TOKEN +"/sendMessage";
    private final String EDIT_MESSAGE_REPLY_MARKUP_URL = "https://api.telegram.org/bot"+ BOT_TOKEN +"/editMessageReplyMarkup";
    private HttpResponse<JsonNode> response;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public SentMessage sendTelegramMessage(SendMessage message){
    this.response = Unirest.post(SEND_MESSAGE_URL)
            .header("Content-Type", "application/json")
            .body(gson.toJson(message))
            .asJson();
    log.debug("BODY SENT BY sendTelegramMessage: "+gson.toJson(message));
    log.debug(printResponse("sendTelegramMessage"));
    return gson.fromJson(response.getBody().toString(),SentMessage.class);
}
    public SentMessage editTelegramMessage(EditMessage message){
            this.response = Unirest.post("https://api.telegram.org/bot"+ BOT_TOKEN +"/editMessageText")
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(message))
                    .asJson();
            log.debug("BODY SENT by editTelegramMessage: "+gson.toJson(message));
            log.debug(printResponse("editTelegramMessage"));
        return gson.fromJson(response.getBody().toString(),SentMessage.class);
    }
    public void answerCallbackQuery(AnswerCallbackQuery answer){
        this.response = Unirest.post(API_URL+"/answerCallbackQuery")
                .header("Content-Type", "application/json")
                .body(gson.toJson(answer))
                .asJson();
        log.debug("BODY SENT BY answerCallbackQuery : "+gson.toJson(answer));
        log.debug(printResponse("answerCallbackQuery"));
    }
    public HueTokens getHueTokens (HueAuthorization authorization)
    private String printResponse(String invoker){
        return invoker+" RESPONSE STATUS: \r\n" + response.getStatus()
                + " "
                + response.getStatusText()
                + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
                + "\r\nBODY: " + response.getBody().toPrettyString();
    }
}
