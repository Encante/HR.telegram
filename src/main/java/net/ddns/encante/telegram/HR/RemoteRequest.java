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
    Gson gson = new Gson();
    private final String botToken = "***REMOVED***";
    private String sendMessageUrl = "https://api.telegram.org/bot"+ botToken +"/sendMessage?";
// chat id:
//    ja:   5580797031L
//    Yaneczka: 566760042L
//    private SendMessage messageToSend;

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
            SendMessageWithReplyKeyboard messageToSend = new SendMessageWithReplyKeyboard();
            messageToSend.setChat_id(chat_id);
            messageToSend.setText(textMessageToSend);
            String[] names = {"Sing","for","me"};
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup.KeyboardBuilder(3,1,names)
                    .setOneTimeKeyboard(true)
                    .build();
            messageToSend.setReply_markup(keyboardMarkup);
//            Gson gson = new Gson();
            String body = gson.toJson(messageToSend);
            String bodyTest2 = "{\"chat_id\":5580797031,\"disable_web_page_preview\":false,\"disable_notification\":false,\"protect_content\":false,\"allow_sending_without_reply\":false,\"reply_markup\":{\"keyboard\":[[{\"text\":\"Sing\",\"request_contact\":false,\"request_location\":false},{\"text\":\"for\",\"request_contact\":false,\"request_location\":false},{\"text\":\"me\",\"request_contact\":false,\"request_location\":false}]],\"resize_keyboard\":false,\"one_time_keyboard\":true,\"selective\":false},\"is_topic_message\":false,\"is_automatic_forward\":false,\"has_protected_content\":false,\"text\":\"So\",\"delete_chat_photo\":false,\"group_chat_created\":false,\"supergroup_chat_created\":false,\"channel_chat_created\":false}";
            String bodyRemove="{\n" +
                    "  \"chat_id\": 5580797031,\n" +
                    "  \"disable_web_page_preview\": false,\n" +
                    "  \"disable_notification\": false,\n" +
                    "  \"protect_content\": false,\n" +
                    "  \"allow_sending_without_reply\": false,\n" +
                    "  \"reply_markup\": {\n" +
                    "    \"remove_keyboard\":true\n" +
                    "  },\n" +
                    "  \"is_topic_message\": false,\n" +
                    "  \"is_automatic_forward\": false,\n" +
                    "  \"has_protected_content\": false,\n" +
                    "  \"text\": \"Remove\",\n" +
                    "  \"delete_chat_photo\": false,\n" +
                    "  \"group_chat_created\": false,\n" +
                    "  \"supergroup_chat_created\": false,\n" +
                    "  \"channel_chat_created\": false\n" +
                    "}";
            sendMessageObject(body);
        }
        case "no" -> {
            SendMessageWithReplyKeyboard messageToSend = new SendMessageWithReplyKeyboard();
            messageToSend.setChat_id(chat_id);
            messageToSend.setText(textMessageToSend);
            String body =gson.toJson(messageToSend);
            sendMessageObject(body);
        }
    }
    return "send message to chatid by object";
}
private void sendMessageObject(String body){
    HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl)
            .header("Content-Type", "application/json")
            .body(body)
            .asJson();
    System.out.println("BODY to: "+body);
    printResponseJsonToConsole(response);
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
        SendMessageWithInlineKeyboard message = new SendMessageWithInlineKeyboard();
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
