package net.ddns.encante.telegram.HR.TelegramMethods;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerCallbackQuery {
    String callback_query_id;
    String text;
    Boolean show_alert;
    String url;
    Long cache_time;

    public AnswerCallbackQuery (String callbackQueryId, String text, Boolean showAlert){
        this.callback_query_id = callbackQueryId;
        this.text = text;
        this.show_alert = showAlert;
    }
}
