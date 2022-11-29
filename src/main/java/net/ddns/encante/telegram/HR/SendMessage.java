package net.ddns.encante.telegram.HR;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

import java.util.ArrayList;

@Getter @Setter
//klasa na obiekty wysyłające dane
public class SendMessage {
    @NonNull
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
}

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendMessageWithInlineKeyboard extends SendMessage {
    InlineKeyboardMarkup reply_markup;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendMessageWithReplyKeyboard extends SendMessage {
    ReplyKeyboardMarkup reply_markup;
}

