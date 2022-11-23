package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
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
public void testSendMessageWithKeyboardToChatId(Long chat_id){
    InlineKeyboardButton a = new InlineKeyboardButton(); a.setCallback_data("a"); a.setText("A");
    InlineKeyboardButton b = new InlineKeyboardButton();b.setCallback_data("b"); b.setText("B");
    InlineKeyboardButton c = new InlineKeyboardButton();c.setCallback_data("c"); c.setText("C");
    ArrayList<InlineKeyboardButton> col1 = new ArrayList<InlineKeyboardButton>();
    col1.add(a);
    col1.add(b);
    col1.add(c);
    ArrayList<ArrayList<InlineKeyboardButton>> keyz = new ArrayList<>();
    keyz.add(col1);
    InlineKeyboardMarkup testInlineKeyboardMarkup = new InlineKeyboardMarkup();
    testInlineKeyboardMarkup.setInline_keyboard(keyz);
    SendMessageWithInlineKeyboard message = new SendMessageWithInlineKeyboard();
    message.setChat_id(5580797031L);
    message.setText("Działa");
    message.setReply_markup(testInlineKeyboardMarkup);
    Gson gson = new Gson();
    String body = gson.toJson(message);
    System.out.println(body);
    HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl)
            .header("Content-Type", "application/json")
            .body(body)
            .asJson();
    printResponseJsonToConsole(response);
}
    public void testKochana(Long chat_id){
        InlineKeyboardButton wiemButton = new InlineKeyboardButton(); wiemButton.setCallback_data("Wiem"); wiemButton.setText("Wiem!");
        InlineKeyboardButton oczywiscieButton = new InlineKeyboardButton();oczywiscieButton.setCallback_data("Oczywiscie"); oczywiscieButton.setText("Oczywiscie!");
        InlineKeyboardButton jeszczeJakButton = new InlineKeyboardButton();jeszczeJakButton.setCallback_data("Jeszcze jak"); jeszczeJakButton.setText("Jeszcze jak!");
        ArrayList<InlineKeyboardButton> col1 = new ArrayList<InlineKeyboardButton>();
        col1.add(wiemButton);
        col1.add(oczywiscieButton);
        col1.add(jeszczeJakButton);
        ArrayList<ArrayList<InlineKeyboardButton>> row1 = new ArrayList<>();
        row1.add(col1);
        InlineKeyboardMarkup testInlineKeyboardMarkup = new InlineKeyboardMarkup();
        testInlineKeyboardMarkup.setInline_keyboard(row1);
        SendMessageWithInlineKeyboard message = new SendMessageWithInlineKeyboard();
        message.setChat_id(chat_id);
        message.setText("Hej czy wiesz ze jestes najkochansza osoba na swiecie?");
        message.setReply_markup(testInlineKeyboardMarkup);
        Gson gson = new Gson();
        String body = gson.toJson(message);
        HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl)
                .header("Content-Type", "application/json")
                .body(body)
                .asJson();
        printResponseJsonToConsole(response);
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
