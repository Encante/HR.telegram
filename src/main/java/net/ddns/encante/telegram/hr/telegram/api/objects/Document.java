package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Document{
    String file_id;
    String file_unique_id;
    PhotoSize thumb;
    String file_name;
    String mime_type;
    Long file_size;
}
