package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageEntity{
    String type;
    Long offset;
    Long lenght;
    String url;
    User user;
    String language;
    String custom_emoji_id;
}