package net.ddns.encante.telegram.HR.TelegramMethods;

import lombok.Getter;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.MessageEntity;
import net.ddns.encante.telegram.HR.TelegramObjects.ReplyKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.ReplyKeyboardRemove;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Getter
@Component
//klasa na obiekty wysyłające dane
// chat id:
//    M:   5580797031L
//    Y:   566760042L
public class SendMessage {
    private Long chat_id;
    private Long message_thread_id;
    private String text;
    private String parse_mode;
    private ArrayList<MessageEntity> entities;
    private boolean disable_web_page_preview;
    private boolean disable_notification;
    private boolean protect_content;
    private Long reply_to_message_id;
    private boolean allow_sending_without_reply;
    private Object reply_markup;

//    transient final Gson gson = new Gson();

//    public void sendMessageWithKeyboard(@NotNull ReplyKeyboardType keyboardType){
//        switch (keyboardType){
//            case INLINE -> {
//                String[] names = {"Inline","she","goes"};
//                SentMessage sentMessage = sendMessageWithInlineKeyboard(new InlineKeyboardMarkup.KeyboardBuilder(3,1,names)
//                        .build()).send();
//                System.out.println("MESSAGE ID IS: "+ sentMessage.getResult().getMessage_id());
//            }
//            case REPLY -> {
//                String[] names = {"Way","she","goes"};
//                sendMessageWithReplyKeyboard(new ReplyKeyboardMarkup.KeyboardBuilder(3,1,names)
//                        .build()).send();
//            }
//            case REMOVE -> {
//                sendMessageWithRemoveKeyboard(new ReplyKeyboardRemove())
//                        .send();
//            }
//            case NO -> send();
//        }
//    }

//@Autowired
//public SendMessage(UnirestRequest request){
//    this.request = request;
//}

    public SendMessage sendMessageWithInlineKeyboard(InlineKeyboardMarkup keyboardMarkup){
        this.reply_markup = keyboardMarkup;
        return this;
    }
    public SendMessage sendMessageWithReplyKeyboard(ReplyKeyboardMarkup keyboardMarkup){
        this.reply_markup = keyboardMarkup;
        return this;
    }
    public SendMessage sendMessageWithRemoveKeyboard(ReplyKeyboardRemove keyboardRemove){
        this.reply_markup = keyboardRemove;
        return this;
    }
    public SendMessage toMe(){
        this.chat_id = 5580797031L;
        return this;
    }
//    public SentMessage send(){
//        return request.sendTelegramMessage(this);
//    }

    public SendMessage setChat_id(Long chat_id) {
        this.chat_id = chat_id;
        return this;
    }

    public SendMessage setMessage_thread_id(Long message_thread_id) {
        this.message_thread_id = message_thread_id;
        return this;
    }

    public SendMessage setText(String text) {
        this.text = text;
        return this;
    }

    public SendMessage setParse_mode(String parse_mode) {
        this.parse_mode = parse_mode;
        return this;
    }

    public SendMessage setEntities(ArrayList<MessageEntity> entities) {
        this.entities = entities;
        return this;
    }

    public SendMessage setDisable_web_page_preview(boolean disable_web_page_preview) {
        this.disable_web_page_preview = disable_web_page_preview;
        return this;
    }

    public SendMessage setDisable_notification(boolean disable_notification) {
        this.disable_notification = disable_notification;
        return this;
    }

    public SendMessage setProtect_content(boolean protect_content) {
        this.protect_content = protect_content;
        return this;
    }

    public SendMessage setReply_to_message_id(Long reply_to_message_id) {
        this.reply_to_message_id = reply_to_message_id;
        return this;
    }

    public SendMessage setAllow_sending_without_reply(boolean allow_sending_without_reply) {
        this.allow_sending_without_reply = allow_sending_without_reply;
        return this;
    }

    public SendMessage setReply_markup(Object reply_markup) {
        this.reply_markup = reply_markup;
        return this;
    }
}




