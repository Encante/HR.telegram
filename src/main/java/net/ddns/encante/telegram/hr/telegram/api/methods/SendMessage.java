package net.ddns.encante.telegram.hr.telegram.api.methods;

import lombok.Getter;
import net.ddns.encante.telegram.hr.telegram.api.objects.MessageEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Getter
@Component
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




