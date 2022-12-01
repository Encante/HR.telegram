package net.ddns.encante.telegram.HR;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditMessage {
    Long chat_id;
    Long message_id;
    String inline_message_id;
    @NonNull
    String text;
    String parse_mode;
    ArrayList<MessageEntity> entities;
    boolean disable_web_page_preview;
    InlineKeyboardMarkup reply_markup;


}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class EditMessageText extends EditMessage{

}
