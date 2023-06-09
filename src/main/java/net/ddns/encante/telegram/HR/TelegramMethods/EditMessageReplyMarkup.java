package net.ddns.encante.telegram.HR.TelegramMethods;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditMessageReplyMarkup {
    Long chat_id;
    Long message_id;
    Long inline_message_id;
    InlineKeyboardMarkup reply_markup;

    public EditMessageReplyMarkup (Long chat_id, Long message_id, InlineKeyboardMarkup reply_markup){
        this.chat_id = chat_id;
        this.message_id = message_id;
        this.reply_markup = reply_markup;
    }
}
