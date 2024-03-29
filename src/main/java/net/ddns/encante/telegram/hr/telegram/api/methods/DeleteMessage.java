package net.ddns.encante.telegram.hr.telegram.api.methods;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteMessage {
    Long chat_id;
    Long message_id;
}
