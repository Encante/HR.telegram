package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLOutput;
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
    InlineKeyboardButton a = new InlineKeyboardButton(); a.setText("a");
    InlineKeyboardButton b = new InlineKeyboardButton(); b.setText("b");
    InlineKeyboardButton c = new InlineKeyboardButton(); c.setText("c");
    ArrayList<InlineKeyboardButton> col1 = new ArrayList<InlineKeyboardButton>();
    col1.add(a);
    col1.add(b);
    col1.add(c);
    ArrayList<ArrayList<InlineKeyboardButton>> keyz = new ArrayList<>();
    keyz.add(col1);
    InlineKeyboardMarkup testInlineKeyboardMarkup = new InlineKeyboardMarkup();
    testInlineKeyboardMarkup.setInline_keyboard(keyz);
    Chat chat = new Chat();
    chat.setId(5580797031L);chat.setType("private");
    System.out.println(chat.getId());
    Message msg = new Message();
    msg.setChat(chat);msg.setText("Hohoho");msg.setReply_markup(testInlineKeyboardMarkup);
    System.out.println(msg.getChat().getId());
    Gson gson = new Gson();
    String body = gson.toJson(msg)  ;
//    String keyboardJson = gson.toJson(testInlineKeyboardMarkup);
    HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl)
            .header("Content-Type", "application/json")
            .body(body)
            .asJson();
    System.out.println(body);
    printResponseJsonToConsole(response);
//    System.out.println(keyboardJson);
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
