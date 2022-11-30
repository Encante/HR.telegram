package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.util.ArrayList;

public class RemoteRequest {
    private final String botToken = "***REMOVED***";
    private final String sendMessageUrl = "https://api.telegram.org/bot"+ botToken +"/sendMessage?";


public void sendMessageAsJson(String body){
    HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl)
            .header("Content-Type", "application/json")
            .body(body)
            .asJson();
    System.out.println("BODY to: "+body);
    printResponseJsonToConsole(response);
//    return this;
}
    public void testKochana(Gson gson, Long chat_id){
        SendInlineKeyboardButton wiemButton = new SendInlineKeyboardButton(); wiemButton.setCallback_data("Wiem"); wiemButton.setText("Wiem!");
        SendInlineKeyboardButton oczywiscieButton = new SendInlineKeyboardButton();oczywiscieButton.setCallback_data("Oczywiscie"); oczywiscieButton.setText("Oczywiscie!");
        SendInlineKeyboardButton jeszczeJakButton = new SendInlineKeyboardButton();jeszczeJakButton.setCallback_data("Jeszcze jak"); jeszczeJakButton.setText("Jeszcze jak!");
        ArrayList<SendInlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(wiemButton);
        row1.add(oczywiscieButton);
        row1.add(jeszczeJakButton);
        ArrayList<ArrayList<SendInlineKeyboardButton>> col1 = new ArrayList<>();
        col1.add(row1);
        SendInlineKeyboardMarkup testInlineKeyboardMarkup = new SendInlineKeyboardMarkup(col1);
        SendMessageWithInlineKeyboard message = new SendMessageWithInlineKeyboard(chat_id,"Hej czy wiesz ze jestes najkochansza osoba na swiecie?",testInlineKeyboardMarkup);
        String body = gson.toJson(message);
        HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl)
                .header("Content-Type", "application/json")
                .body(body)
                .asJson();
        printResponseJsonToConsole(response);
    }
//void printResponseStringToConsole(HttpResponse<String> response){
//    System.out.println("RESPONSE STATUS: \r\n" + response.getStatus()
//            + " "
//            + response.getStatusText()
//            + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
//            + "\r\nBODY: " + response.getBody());
//}

private void printResponseJsonToConsole(HttpResponse<JsonNode> response){
    System.out.println("RESPONSE STATUS: \r\n" + response.getStatus()
            + " "
            + response.getStatusText()
            + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
            + "\r\nBODY: " + response.getBody().toPrettyString());
}

}
