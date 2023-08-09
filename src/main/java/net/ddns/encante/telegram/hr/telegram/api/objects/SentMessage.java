package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.hr.telegram.entity.Message;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SentMessage {
    boolean ok;
    Message result;
}