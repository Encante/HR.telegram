package net.ddns.encante.telegram.HR;

import com.google.gson.Gson;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collections;

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
                sendMessageWithInlineKeyboard(new SendInlineKeyboardMarkup.KeyboardBuilder(3,1,names)
                        .build()).send();
            }
            case REPLY -> {
                String[] names = {"Way","she","goes"};
                sendMessageWithReplyKeyboard(new SendReplyKeyboardMarkup.KeyboardBuilder(3,1,names)
                        .build()).send();
            }
            case REMOVE -> {
                sendMessageWithRemoveKeyboard(new SendReplyKeyboardRemove())
                        .send();
            }
            case NO -> send();
        }
    }

    SendMessage sendMessageWithInlineKeyboard(SendInlineKeyboardMarkup keyboardMarkup){
        this.reply_markup = keyboardMarkup;
        return this;
    }
    SendMessage sendMessageWithReplyKeyboard(SendReplyKeyboardMarkup keyboardMarkup){
        this.reply_markup = keyboardMarkup;
        return this;
    }
    SendMessage sendMessageWithRemoveKeyboard(SendReplyKeyboardRemove keyboardRemove){
        this.reply_markup = keyboardRemove;
        return this;
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
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendReplyKeyboardMarkup{
@org.springframework.lang.NonNull
ArrayList<ArrayList<SendKeyboardButton>> keyboard;
boolean resize_keyboard;
boolean one_time_keyboard;
String input_field_placeholder;
boolean selective;
SendReplyKeyboardMarkup(KeyboardBuilder builder){
    this.keyboard = builder.keyboardLayout;
    this.resize_keyboard = builder.resize_keyboard;
    this.one_time_keyboard = builder.one_time_keyboard;
    this.input_field_placeholder = builder.input_field_placeholder;
    this.selective = builder.selective;
}
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class KeyboardBuilder {
    @org.springframework.lang.NonNull
    int rows;
    @org.springframework.lang.NonNull
    int cols;
    @NonNull
    ArrayList<String> names;
    ArrayList<SendKeyboardButton> rowx;
    ArrayList<ArrayList<SendKeyboardButton>> keyboardLayout;
    //        optional
    boolean resize_keyboard;
    boolean one_time_keyboard;
    String input_field_placeholder;
    boolean selective;

    public KeyboardBuilder(int rows, int cols, String ...namesStream){
        this.rows=rows;
        this.cols=cols;
        this.names=new ArrayList<>();
        Collections.addAll(this.names, namesStream);
    }
    public KeyboardBuilder setResizeKeyboard(boolean resizeKeyboard){
        this.resize_keyboard = resizeKeyboard;
        return this;
    }
    public KeyboardBuilder setOneTimeKeyboard(boolean oneTimeKeyboard){
        this.one_time_keyboard = oneTimeKeyboard;
        return this;
    }
    public KeyboardBuilder setInputFieldPlaceholder(String inputFieldPlaceholder){
        this.input_field_placeholder = inputFieldPlaceholder;
        return this;
    }
    public KeyboardBuilder setSelective(boolean selective){
        this.selective = selective;
        return this;
    }
    public SendReplyKeyboardMarkup build(){
        this.rowx=new ArrayList<>();
        this.keyboardLayout =new ArrayList<>();
//            check if there is suitable names for all buttons
        if(rows*cols == names.size()) {
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    this.rowx.add(new SendKeyboardButton(this.names.get(0)));
                    this.names.remove(0);
                }
                this.keyboardLayout.add(this.rowx);
                this.rowx = new ArrayList<>();
            }
            return new SendReplyKeyboardMarkup(this);
        }
        else{
            System.out.println("Not enough names for all buttons.");
        }
        return new SendReplyKeyboardMarkup(this);
        }
    }
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendInlineKeyboardMarkup{
    @org.springframework.lang.NonNull
    ArrayList<ArrayList<SendInlineKeyboardButton>> inline_keyboard;
    SendInlineKeyboardMarkup(KeyboardBuilder builder){
        this.inline_keyboard = builder.keyboardLayout;
    }
    SendInlineKeyboardMarkup(ArrayList<ArrayList<SendInlineKeyboardButton>> inline_keyboard){
        this.inline_keyboard=inline_keyboard;
    }
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class KeyboardBuilder {
        @org.springframework.lang.NonNull
        int rows;
        @org.springframework.lang.NonNull
        int cols;
        @NonNull
        ArrayList<String> names;
        ArrayList<SendInlineKeyboardButton> rowx;
        ArrayList<ArrayList<SendInlineKeyboardButton>> keyboardLayout;

        public KeyboardBuilder(int rows, int cols, String ...namesStream){
            this.rows=rows;
            this.cols=cols;
            this.names=new ArrayList<>();
            Collections.addAll(this.names, namesStream);
        }
        public SendInlineKeyboardMarkup build(){
            this.rowx=new ArrayList<>();
            this.keyboardLayout =new ArrayList<>();
//            check if there is suitable names for all buttons
            if(rows*cols == names.size()) {
                for (int i = 0; i < cols; i++) {
                    for (int j = 0; j < rows; j++) {
                        this.rowx.add(new SendInlineKeyboardButton(this.names.get(0)));
                        this.names.remove(0);
                    }
                    this.keyboardLayout.add(this.rowx);
                    this.rowx = new ArrayList<>();
                }
                return new SendInlineKeyboardMarkup(this);
            }
            else{
                System.out.println("Not enough names for all buttons.");
            }
            return new SendInlineKeyboardMarkup(this);
        }
    }
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendReplyKeyboardRemove{
    boolean remove_keyboard=true;
    boolean selective;
}

@Getter @Setter @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendKeyboardButton{
    @NonNull
    String text;
    boolean request_contact;
    boolean request_location;
    KeyboardButtonPollType request_poll;
    WebAppInfo web_app;
    public SendKeyboardButton(@NotNull String text){
        this.text = text;
    }
}
@NoArgsConstructor
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendInlineKeyboardButton {
    String text;
    String url;
    String callback_data;
    WebAppInfo web_app;
    LoginUrl login_url;
    String switch_inline_query;
    String switch_inline_query_current_chat;
    CallbackGame callback_game;
    boolean pay;

    public SendInlineKeyboardButton(String keyText) {
        this.text = keyText;
        this.callback_data=text;
    }
    public SendInlineKeyboardButton(String keyText, String callbackData){
        this.text=keyText;
        this.callback_data=callbackData;
    }
}
