package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

public class RemoteRequest {
    Gson gson = new Gson();
    private final String botToken = "***REMOVED***";
    private String sendMessageUrl = "https://api.telegram.org/bot"+ botToken +"/sendMessage?";


public void sendMessageAsJson(String body){
    HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl)
            .header("Content-Type", "application/json")
            .body(body)
            .asJson();
    System.out.println("BODY to: "+body);
    printResponseJsonToConsole(response);
}
//    public void testKochana(Gson gson,Long chat_id){
//        InlineKeyboardButton wiemButton = new InlineKeyboardButton(); wiemButton.setCallback_data("Wiem"); wiemButton.setText("Wiem!");
//        InlineKeyboardButton oczywiscieButton = new InlineKeyboardButton();oczywiscieButton.setCallback_data("Oczywiscie"); oczywiscieButton.setText("Oczywiscie!");
//        InlineKeyboardButton jeszczeJakButton = new InlineKeyboardButton();jeszczeJakButton.setCallback_data("Jeszcze jak"); jeszczeJakButton.setText("Jeszcze jak!");
//        ArrayList<InlineKeyboardButton> row1 = new ArrayList<>();
//        row1.add(wiemButton);
//        row1.add(oczywiscieButton);
//        row1.add(jeszczeJakButton);
//        ArrayList<ArrayList<InlineKeyboardButton>> col1 = new ArrayList<>();
//        col1.add(row1);
//        InlineKeyboardMarkup testInlineKeyboardMarkup = new InlineKeyboardMarkup();
//        testInlineKeyboardMarkup.setInline_keyboard(col1);
//        SendMessageWithInlineKeyboard message = new SendMessageWithInlineKeyboard();
//        message.setChat_id(chat_id);
//        message.setText("Hej czy wiesz ze jestes najkochansza osoba na swiecie?");
//        message.setReply_markup(testInlineKeyboardMarkup);
//        String body = gson.toJson(message);
//        HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl)
//                .header("Content-Type", "application/json")
//                .body(body)
//                .asJson();
//        printResponseJsonToConsole(response);
//    }
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

}
