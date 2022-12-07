package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InlineKeyboardButton {
    String text;
    String url;
    String callback_data;
    WebAppInfo web_app;
    LoginUrl login_url;
    String switch_inline_query;
    String switch_inline_query_current_chat;
    CallbackGame callback_game;
    boolean pay;

    public InlineKeyboardButton(String keyText) {
        this.text = keyText;
        this.callback_data=text;
    }
    public InlineKeyboardButton(String keyText, String callbackData){
        this.text=keyText;
        this.callback_data=callbackData;
    }
}
