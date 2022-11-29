package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Getter @Setter
@NoArgsConstructor
//klasa na obiekty wysyłające dane
// chat id:
//    ja:   5580797031L
//    Yaneczka: 566760042L
//    private SendMessage messageToSend;
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

    public String sendMessageToChatIdByObject(@NotNull ReplyKeyboardType keyboardType){
        RemoteRequest request = new RemoteRequest();
        Gson gson = request.gson;
        switch (keyboardType){
            case INLINE -> System.out.println("inline keyboard placeholder");
            case REPLY -> {
                SendMessageWithReplyKeyboard messageToSend = new SendMessageWithReplyKeyboard(chat_id,text);
                String[] names = {"Sing","for","me"};
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup.KeyboardBuilder(3,1,names)
                        .setOneTimeKeyboard(true)
                        .build();
                messageToSend.setReply_markup(keyboardMarkup);
                String body = gson.toJson(messageToSend);
                request.sendMessageAsJson(body);
            }
            case NO -> {
                SendMessageWithReplyKeyboard messageToSend = new SendMessageWithReplyKeyboard(chat_id,text);
                String body =gson.toJson(messageToSend);
                request.sendMessageAsJson(body);
            }
        }
        return "send message to chatid by object";
    }
    void sendUpdateToChatId(WebhookUpdate update,Long chatId){
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
                    + "  CONTENT: "
                    + update.message.getText();
        }
        sendMessageToChatIdByObject(ReplyKeyboardType.NO);
    }
}
enum ReplyKeyboardType{
    INLINE,
    REPLY,
    REMOVE,
    FORCE,
    NO
}

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendMessageWithInlineKeyboard {
    @NotNull
    Long chat_id;
    Long message_thread_id;
    @NotNull
    String text;
    String parse_mode;
    ArrayList<MessageEntity> entities;
    boolean disable_web_page_preview;
    boolean disable_notification;
    boolean protect_content;
    Long reply_to_message_id;
    boolean allow_sending_without_reply;

    InlineKeyboardMarkup reply_markup;
    public SendMessageWithInlineKeyboard (@NotNull Long chat_id, @NotNull String text){
        this.chat_id = chat_id;
        this.text = text;
    }
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendMessageWithReplyKeyboard {
    @NotNull
    Long chat_id;
    Long message_thread_id;
    @NotNull
    String text;
    String parse_mode;
    ArrayList<MessageEntity> entities;
    boolean disable_web_page_preview;
    boolean disable_notification;
    boolean protect_content;
    Long reply_to_message_id;
    boolean allow_sending_without_reply;
    ReplyKeyboardMarkup reply_markup;

    public SendMessageWithReplyKeyboard(@NotNull Long chat_id, @NotNull String text){
        this.chat_id = chat_id;
        this.text = text;
    }
}

