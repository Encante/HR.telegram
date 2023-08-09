package net.ddns.encante.telegram.hr.telegram.api.methods;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.hr.telegram.api.objects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.hr.telegram.api.objects.MessageEntity;

import java.util.ArrayList;
@Builder
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
}
