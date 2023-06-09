package net.ddns.encante.telegram.HR.TelegramMethods;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.HR.TelegramObjects.CallbackQuery;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.MessageEntity;

import java.util.ArrayList;
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditMessageText {
    Long chat_id;
    Long message_id;
    String inline_message_id;
    String text;
    String parse_mode;
    ArrayList<MessageEntity> entities;
    boolean disable_web_page_preview;
    InlineKeyboardMarkup reply_markup;


    public EditMessageText(Long chatId, Long messageId, String text) {
        this.chat_id = chatId;
        this.message_id = messageId;
        this.text = text;
    }
    public EditMessageText(CallbackQuery callback, String text){
        this.chat_id = callback.getMessage().getChat().getId();
        this.message_id = callback.getMessage().getMessage_id();
        this.text = text;
    }
}
