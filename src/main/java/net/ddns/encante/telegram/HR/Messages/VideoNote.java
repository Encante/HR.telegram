package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoNote{
    String file_id;
    String file_unique_id;
    Long lenght;
    Long duration;
    PhotoSize thumb;
    Long file_size;
}
