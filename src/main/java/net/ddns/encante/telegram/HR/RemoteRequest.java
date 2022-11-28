package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class RemoteRequest {
    private final String botToken = "***REMOVED***";
    String sendMessageUrl = "https://api.telegram.org/bot"+ botToken +"/sendMessage?";
// chat id:
//    ja:   5580797031L
//    Yaneczka: 566760042L
public String sendMessageToChatIdByString(String messageToSend, Long chat_id ){
    HttpResponse<String> response = Unirest.post(sendMessageUrl+
            "chat_id="+
            chat_id+
            "&text="+
            messageToSend)
            .asString();
    printResponseStringToConsole(response);
    return response.toString();
}
public String sendMessageToChatIdByObject(String textMessageToSend, @NotNull String replyKeyboardType, @NotNull Long chat_id){
    switch (replyKeyboardType){
        case "inline" ->{
            System.out.println("inline keyboard placeholder");
        }
        case "reply" -> {
            System.out.println("REPLY KEYBOARD PLACEHOLDER");
            MessageWithReplyKeyboard messageToSend = new MessageWithReplyKeyboard();
            messageToSend.setChat_id(chat_id);
            messageToSend.setText(textMessageToSend);
            String[] names = {"Sing","for","me"};
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            ReplyKeyboardMarkup.KeyboardBuilder builder = keyboardMarkup.new KeyboardBuilder(3,1);
            builder.factory(names);
            keyboardMarkup.setKeyboard(builder.getOut());
            keyboardMarkup.setOne_time_keyboard(true);
            messageToSend.setReply_markup(keyboardMarkup);
            Gson gson = new Gson();
            String body = gson.toJson(messageToSend);
            String bodyTest = "{\n" +
                    "  \"chat_id\": 5580797031,\n" +
                    "  \"reply_markup\": {\n" +
                    "    \"resize_keyboard\": true,\n" +
                    "    \"keyboard\": [\n" +
                    "      [\n" +
                    "        \"Button 1\",\n" +
                    "        \"Button 2\"\n" +
                    "      ]\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  \"text\": \"So\"\n" +
                    "}";
            HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl)
                    .header("Content-Type", "application/json")
                    .body(bodyTest)
                    .asJson();
            System.out.println("BODY to: "+body);
            printResponseJsonToConsole(response);
        }
    }
    return "send message to chatid by object";
}
    public void testKochana(Gson gson,Long chat_id){
        InlineKeyboardButton wiemButton = new InlineKeyboardButton(); wiemButton.setCallback_data("Wiem"); wiemButton.setText("Wiem!");
        InlineKeyboardButton oczywiscieButton = new InlineKeyboardButton();oczywiscieButton.setCallback_data("Oczywiscie"); oczywiscieButton.setText("Oczywiscie!");
        InlineKeyboardButton jeszczeJakButton = new InlineKeyboardButton();jeszczeJakButton.setCallback_data("Jeszcze jak"); jeszczeJakButton.setText("Jeszcze jak!");
        ArrayList<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(wiemButton);
        row1.add(oczywiscieButton);
        row1.add(jeszczeJakButton);
        ArrayList<ArrayList<InlineKeyboardButton>> col1 = new ArrayList<>();
        col1.add(row1);
        InlineKeyboardMarkup testInlineKeyboardMarkup = new InlineKeyboardMarkup();
        testInlineKeyboardMarkup.setInline_keyboard(col1);
        MessageWithInlineKeyboard message = new MessageWithInlineKeyboard();
        message.setChat_id(chat_id);
        message.setText("Hej czy wiesz ze jestes najkochansza osoba na swiecie?");
        message.setReply_markup(testInlineKeyboardMarkup);
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
