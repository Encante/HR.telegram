package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallbackQuery{
    String id;
    User from;
    Message message;
    String inline_message_id;
    String chat_instance;
    String data;
    String game_short_name;
}