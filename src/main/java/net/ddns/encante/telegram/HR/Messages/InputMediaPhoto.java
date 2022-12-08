package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InputMediaPhoto {
    String type;
    String media;
    String caption;
    String parse_mode;
    ArrayList<MessageEntity> caption_entities;
}
