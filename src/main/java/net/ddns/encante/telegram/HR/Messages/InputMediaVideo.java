package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InputMediaVideo {
    String type;
    String media;
    String thumb;
    String caption;
    String parse_mode;
    ArrayList<MessageEntity> caption_entities;
    Long width;
    Long height;
    Long duration;
    boolean supports_streaming;
}
