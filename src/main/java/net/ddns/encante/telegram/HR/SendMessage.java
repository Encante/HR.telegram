package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Getter @Builder
//klasa na obiekty wysyłające dane
// chat id:
//    M:   5580797031L
//    Y:   566760042L
public class SendMessage {
    @NotNull
    Long chat_id;
    Long message_thread_id;
    String text;
    String parse_mode;
    ArrayList<MessageEntity> entities;
    boolean disable_web_page_preview;
    boolean disable_notification;
    boolean protect_content;
    Long reply_to_message_id;
    boolean allow_sending_without_reply;
    Object reply_markup;

    transient final RemoteRequest request = new RemoteRequest();
    transient final Gson gson = new Gson();

    public void sendMessageWithKeyboard(@NotNull ReplyKeyboardType keyboardType){
        switch (keyboardType){
            case INLINE -> {
                String[] names = {"Inline","she","goes"};
                CatchedMessage catchedMessage = sendMessageWithInlineKeyboard(new InlineKeyboardMarkup.KeyboardBuilder(3,1,names)
                        .build()).send();
                System.out.println("MESSAGE ID IS: "+catchedMessage.getResult().getMessage_id());
            }
            case REPLY -> {
                String[] names = {"Way","she","goes"};
                sendMessageWithReplyKeyboard(new ReplyKeyboardMarkup.KeyboardBuilder(3,1,names)
                        .build()).send();
            }
            case REMOVE -> {
                sendMessageWithRemoveKeyboard(new ReplyKeyboardRemove())
                        .send();
            }
            case NO -> send();
        }
    }

    SendMessage sendMessageWithInlineKeyboard(InlineKeyboardMarkup keyboardMarkup){
        this.reply_markup = keyboardMarkup;
        return this;
    }
    SendMessage sendMessageWithReplyKeyboard(ReplyKeyboardMarkup keyboardMarkup){
        this.reply_markup = keyboardMarkup;
        return this;
    }
    SendMessage sendMessageWithRemoveKeyboard(ReplyKeyboardRemove keyboardRemove){
        this.reply_markup = keyboardRemove;
        return this;
    }
    SendMessage sendToMe (){
        this.chat_id = 5580797031L;
        send();
        return this;
    }
//    SendMessage send(){
//        String body =gson.toJson(this);
//        request.sendMessageAsJson(body);
//        return this;
//    }
    CatchedMessage send(){
        String body =gson.toJson(this);
        CatchedMessage response = gson.fromJson(request.sendMessageAsJson(body).getBody().toString(), CatchedMessage.class);
        return response;
    }
    void sendTextUpdateToChatId(WebhookUpdate update, Long chatId){
        this.chat_id = chatId;
        if (update.message.getText()!= null) {
            this.text = "New message! T: " + Utils.getCurrentDateTime()
                    + "  FROM: "
                    + update.message.getFrom().getFirst_name()
                    + " "
                    + update.message.getFrom().getLast_name()
                    + "  CHAT ID: "
                    + update.message.getChat().getId()
                    + "  CONTENT: "
                    + update.message.getText();
        }
        else {
            this.text = "New message! T: " + Utils.getCurrentDateTime()
                    + "  FROM: "
                    + update.message.getFrom().getFirst_name()
                    + " "
                    + update.message.getFrom().getLast_name()
                    + "  CHAT ID: "
                    + update.message.getChat().getId()
                    + " But it has no text!";
        }
        send();
    }
}
enum ReplyKeyboardType{
    INLINE,
    REPLY,
    REMOVE,
    FORCE,
    NO
}




