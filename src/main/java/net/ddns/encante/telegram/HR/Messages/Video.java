package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Video{
    String file_id;
    String file_unique_id;
    Long width;
    Long height;
    Long duration;
    PhotoSize thumb;
    String file_name;
    String mime_type;
    Long file_size;
}