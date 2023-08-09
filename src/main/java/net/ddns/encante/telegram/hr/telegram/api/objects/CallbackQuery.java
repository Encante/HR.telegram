package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallbackQuery{
    String id;
    User from; //sender
    Message message; //Optional. Message with the callback button that originated the query. Note that message content and message date will not be available if the message is too old
    String inline_message_id;
    String chat_instance;
    String data;
    String game_short_name;
}