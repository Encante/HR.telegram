package net.ddns.encante.telegram.HR.TelegramObjects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Accessors (chain = true)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForceReply{
    boolean force_reply;
    String input_field_placeholder;
    boolean selective;
}