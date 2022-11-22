package net.ddns.encante.telegram.HR;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class RemoteRequest {
    private final String botToken = "***REMOVED***";
    String sendMessageUrl = "https://api.telegram.org/bot"+ botToken +"/sendMessage?";
// chat id:
//    ja:   5580797031
//    Yana: 566760042

public void sendMessageToChatId(String messageToSend, Long chat_id ){
    HttpResponse<String> response = Unirest.post(sendMessageUrl+
            "chat_id="+
            chat_id+
            "&text="+
            messageToSend)
            .asString();
    printResponseToConsole(response);
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
    HttpResponse<String> response = Unirest.post(sendMessageUrl+
                    "chat_id="+
                    chat_id+
//    System.out.println(testReplyKeyboardMarkup.getKeyboard().get(0).get(0).getText());

}
void printResponseToConsole(HttpResponse<String> response){
    System.out.println("RESPONSE STATUS: \r\n" + response.getStatus()
            + " "
            + response.getStatusText()
            + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
            + "\r\nBODY: " + response.getBody());
}
//    public void setWebhook (String address){
//
//    }

}
