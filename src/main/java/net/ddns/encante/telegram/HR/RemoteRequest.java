package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class RemoteRequest {
    private final String botToken = "***REMOVED***";
    String sendMessageUrl = "https://api.telegram.org/bot"+ botToken +"/sendMessage?";
// chat id:
//    ja:   5580797031
//    Yaneczka: 566760042
    Gson gson = new Gson();
public void sendMessageToChatId(String messageToSend, Long chat_id ){
    HttpResponse<String> response = Unirest.post(sendMessageUrl+
            "chat_id="+
            chat_id+
            "&text="+
            messageToSend)
            .asString();
    printResponseStringToConsole(response);
}
public void sendMessageWithKeyboardToChatId(Long chat_id){
    KeyboardButton a = new KeyboardButton("a");
    KeyboardButton b = new KeyboardButton("b");
    KeyboardButton c = new KeyboardButton("c");
    ArrayList<KeyboardButton> col1 = new ArrayList<KeyboardButton>();
    col1.add(a);
    col1.add(b);
    col1.add(c);
    ArrayList<ArrayList<KeyboardButton>> keyz = new ArrayList<>();
    keyz.add(col1);
    ReplyKeyboardMarkup testReplyKeyboardMarkup = new ReplyKeyboardMarkup(keyz);
    String keyboardJson = gson.toJson(testReplyKeyboardMarkup);
//    HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl + "chat_id=" + chat_id + "&text=Hejka")..asJson();
//    printResponseJsonToConsole(response);
    System.out.println(keyboardJson);
}
void printResponseStringToConsole(HttpResponse<String> response){
    System.out.println("RESPONSE STATUS: \r\n" + response.getStatus()
            + " "
            + response.getStatusText()
            + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
            + "\r\nBODY: " + response.getBody());
}

void printResponseJsonToConsole(HttpResponse<JsonNode> response){
    System.out.println("RESPONSE STATUS: \r\n" + response.getStatus()
            + " "
            + response.getStatusText()
            + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
            + "\r\nBODY: " + response.getBody().toPrettyString());
}
//    public void setWebhook (String address){
//
//    }

}
