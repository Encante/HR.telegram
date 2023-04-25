package net.ddns.encante.telegram.HR.RemoteRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import net.ddns.encante.telegram.HR.Hue.HueDevice;
import net.ddns.encante.telegram.HR.Hue.HueLinkButton;
import net.ddns.encante.telegram.HR.Hue.HueUser;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.SentMessage;
import net.ddns.encante.telegram.HR.Utils;
import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import net.ddns.encante.telegram.HR.persistence.entities.HueTokensEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class UnirestRequest {
    private static final Logger log = LoggerFactory.getLogger(UnirestRequest.class);
    private final String BOT_TOKEN = "XXX";
    private final String HUE_API_URL = "https://api.meethue.com/route";
    private final String HUE_OAUTH_URL = "https://api.meethue.com/v2/oauth2/token";
    private final String TELEGRAM_API_URL = "https://api.telegram.org/bot"+ BOT_TOKEN;
    private final String SEND_MESSAGE_URL = "https://api.telegram.org/bot"+ BOT_TOKEN +"/sendMessage";
    private final String EDIT_MESSAGE_REPLY_MARKUP_URL = "https://api.telegram.org/bot"+ BOT_TOKEN +"/editMessageReplyMarkup";
    private HttpResponse<JsonNode> response;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public SentMessage sendTelegramMessageObj(SendMessage message){
    this.response = Unirest.post(SEND_MESSAGE_URL)
            .header("Content-Type", "application/json")
            .body(gson.toJson(message))
            .asJson();
    log.debug("BODY SENT BY sendTelegramMessageOBJ: "+gson.toJson(message));
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
        this.response = Unirest.post(TELEGRAM_API_URL +"/answerCallbackQuery")
                .header("Content-Type", "application/json")
                .body(gson.toJson(answer))
                .asJson();
        log.debug("BODY SENT BY answerCallbackQuery : "+gson.toJson(answer));
        log.debug(printResponse("answerCallbackQuery"));
    }

    public HueAuthorizationEntity requestHueAuthentication(HueAuthorizationEntity authorization){
//        first part: we exchange :code, clientId, clientSecret we got in authentication for authentication tokens
        if (authorization.getClientId() == null || authorization.getClientSecret() == null || authorization.getCode() == null){
            log.warn("Part of Authorization missing! Check authorization in DB!");
            throw new RuntimeException("Part of Authorization missing! Check authorization in DB!");
        }else {
            String credentials = authorization.getClientId() + ":" + authorization.getClientSecret();
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            this.response = Unirest.post
                            (HUE_OAUTH_URL)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + encodedCredentials)
                    .body("grant_type=authorization_code&code=" + authorization.getCode())
                    .asJson();
            if (standardResponseStatusBodyCheck("Code exchange for tokens")){
                HueTokensEntity tokensEntity = gson.fromJson(response.getBody().toString(), HueTokensEntity.class);
                tokensEntity.setCreated(Utils.getCurrentUnixTime());
                authorization.setTokens(tokensEntity);
            }else {
                throw new RuntimeException("Code exchange for tokens failed!");
            }
//            Now we should have our tokens in object, time to virtually press the button on Hue bridge
            if (authorization.getTokens().getAccess_token() != null && authorization.getDisplayName() != null) {
                this.response = Unirest.put(HUE_API_URL + "/api/0/config")
                        .header("Authorization", "Bearer " + authorization.getTokens().getAccess_token())
                        .header("Content-Type", "application/json")
                        .body("{\"linkbutton\":true}")
                        .asJson();
//                If 'virtual button press' is ok we need to request our app username to manage our hue system
//                check if response is ok before creating HueLinkButton obj.
                if (standardResponseStatusBodyCheck("Virtually press (put) hue button.")){
                    HueLinkButton[] linkButton = gson.fromJson(response.getBody().toString(),HueLinkButton[].class);
//                    check if button is pressed
                    if (linkButton[0].getSuccess()!= null){
                        this.response = Unirest.post(HUE_API_URL+"/api")
                                .header("Authorization", "Bearer " + authorization.getTokens().getAccess_token())
                                .header("Content-Type", "application/json")
                                .body("{\"devicetype\":\""+authorization.getDisplayName()+"\"}")
                                .asJson();
//                        standard response check
                        if (standardResponseStatusBodyCheck("Hue authentication after button is pressed.")){
                            HueUser[] hueUser = gson.fromJson(response.getBody().toString(), HueUser[].class);
                            if (hueUser[0].getSuccess() != null) {
                                authorization.setUsername(hueUser[0].getSuccess().getUsername());
                                return authorization;
                            }else {
                                log.warn("HueUser error: "+hueUser[0].getError().toString());
                                throw new RuntimeException("HueUser error");
                            }
                        }else{
                            throw new RuntimeException("Hue authentication after button is pressed.");
                        }
                    }else {
                        log.warn("HueLinkButton fault. Faultstring: "+ linkButton[0].getFault().getFaultstring()+" errorcode: "+ linkButton[0].getFault().getDetail().getErrorcode());
                        throw new RuntimeException("HueLinkButton fault.");
                    }
                } else {
                    throw new RuntimeException("Virtually press (put) hue button.");
                }
            } else if (authorization.getTokens().getAccess_token() == null) {
                log.warn("Authorization token empty!");
                throw new RuntimeException("Authorization token empty!");
            } else {
                log.warn("No display name set for app!");
                throw new RuntimeException("No display name set for app!");
            }
        }
    }
    public HueTokensEntity refreshHueTokens(HueAuthorizationEntity authorization){
        String credentials = authorization.getClientId() + ":" + authorization.getClientSecret();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        this.response = Unirest.post
                        (HUE_OAUTH_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + encodedCredentials)
                .body("grant_type=refresh_token&refresh_token=" + authorization.getTokens().getRefresh_token())
                .asJson();
        if (standardResponseStatusBodyCheck("Hue Refresh Tokens")){
            return gson.fromJson(response.getBody().toString(),HueTokensEntity.class);
        }else {
            throw new RuntimeException("Hue Refresh Tokens");
        }
    }
    public HueDevice hueGetResourceDeviceId(HueAuthorizationEntity authorization, String deviceId){
        this.response = Unirest.get(HUE_API_URL+"/clip/v2/resource/device/"+deviceId)
                .header("Authorization", "Bearer "+authorization.getTokens().getAccess_token())
                .header("hue-application-key", authorization.getUsername())
                .asJson();
        if (standardResponseStatusBodyCheck("hueGetResourceDeviceId")){
            return gson.fromJson(response.getBody().toString(),HueDevice.class);
        }else return null;
    }

    public HueDevice hueGetResourceDevice(HueAuthorizationEntity authorization){
        this.response = Unirest.get(HUE_API_URL+"/clip/v2/resource/device/")
                .header("Authorization", "Bearer "+authorization.getTokens().getAccess_token())
                .header("hue-application-key", authorization.getUsername())
                .asJson();
        if (standardResponseStatusBodyCheck("hueGetResourceDeviceId")){
            return gson.fromJson(response.getBody().toString(),HueDevice.class);
        }else return null;
    }

//
//    private methods
//
    private String printResponse(String invoker){
        return invoker+" RESPONSE STATUS: \r\n" + response.getStatus()
                + " "
                + response.getStatusText()
                + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
                + "\r\nBODY: " + response.getBody().toPrettyString();
    }

    private boolean standardResponseStatusBodyCheck(String invoker){
        if (this.response.getStatus() == 200 && this.response.getBody()!= null) {
            return true;
        } else if (this.response.getBody() == null) {
            log.warn(invoker+" "+printResponse("Response body is null."));
            return false;
        }else {
            log.warn(invoker+" "+printResponse("Response NOK"));
            return false;
        }
    }
}