package net.ddns.encante.telegram.HR;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditMessage {
    final transient RemoteRequest request=new RemoteRequest();
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

    EditMessage edit(){
        request.editMessageAsJson(this);
                return this;
    }
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class EditMessageReplyMarkup extends EditMessage{
    public EditMessageReplyMarkup(SentMessage sentMessage){
        super(sentMessage.getResult().getChat().getId(),sentMessage.getResult().getMessage_id());
    }
    public EditMessageReplyMarkup(CallbackQuery callback){
        super(callback.getMessage().getChat().getId(),callback.getMessage().getMessage_id());
    }
}
