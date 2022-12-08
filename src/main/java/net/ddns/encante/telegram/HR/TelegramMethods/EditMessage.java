package net.ddns.encante.telegram.HR.TelegramMethods;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.HR.RemoteRequest.UnirestRequest;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditMessage {
    @Autowired
    transient UnirestRequest request;
    Long chat_id;
    Long message_id;
    String inline_message_id;
    String text;
    String parse_mode;
    ArrayList<MessageEntity> entities;
    boolean disable_web_page_preview;
    InlineKeyboardMarkup reply_markup;

    public EditMessage(Long chat_id, Long message_id) {
        this.chat_id = chat_id;
        this.message_id = message_id;
    }

    public EditMessage edit(){
        request.editMessageObject(this);
                return this;
    }
}
