package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.HR.Message;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SentMessage {
    boolean ok;
    Message result;
}