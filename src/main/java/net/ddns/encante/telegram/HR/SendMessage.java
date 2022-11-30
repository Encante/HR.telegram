package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Getter @Builder
//klasa na obiekty wysyłające dane
// chat id:
//    ja:   5580797031L
//    Yaneczka: 566760042L
//    private SendMessage messageToSend;
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

    private transient final RemoteRequest request = new RemoteRequest();
    private transient final Gson gson = new Gson();

    public String sendMessageWithKeyboard(@NotNull ReplyKeyboardType keyboardType){
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
            case NO -> send();
        }
        return "send message to chatid by object";
    }
    SendMessage sendToMe (){
        this.chat_id = 5580797031L;
        send();
        return this;
    }
    SendMessage send(){
        String body =gson.toJson(this);
        request.sendMessageAsJson(body);
        return this;
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

