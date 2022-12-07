package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;
import net.ddns.encante.telegram.HR.Messages.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.Messages.ReplyKeyboardMarkup;
import net.ddns.encante.telegram.HR.Messages.ReplyKeyboardRemove;
import net.ddns.encante.telegram.HR.RemoteRequest.UnirestRequest;

import java.util.ArrayList;

@Getter @Builder
//klasa na obiekty wysyłające dane
// chat id:
//    M:   5580797031L
//    Y:   566760042L
public class SendMessage {
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

    transient final UnirestRequest request = new UnirestRequest();
    transient final Gson gson = new Gson();

//    public void sendMessageWithKeyboard(@NotNull ReplyKeyboardType keyboardType){
//        switch (keyboardType){
//            case INLINE -> {
//                String[] names = {"Inline","she","goes"};
//                SentMessage sentMessage = sendMessageWithInlineKeyboard(new InlineKeyboardMarkup.KeyboardBuilder(3,1,names)
//                        .build()).send();
//                System.out.println("MESSAGE ID IS: "+ sentMessage.getResult().getMessage_id());
//            }
//            case REPLY -> {
//                String[] names = {"Way","she","goes"};
//                sendMessageWithReplyKeyboard(new ReplyKeyboardMarkup.KeyboardBuilder(3,1,names)
//                        .build()).send();
//            }
//            case REMOVE -> {
//                sendMessageWithRemoveKeyboard(new ReplyKeyboardRemove())
//                        .send();
//            }
//            case NO -> send();
//        }
//    }

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
    SentMessage send(){
        request.sendMessageObject(this);
    }
}




