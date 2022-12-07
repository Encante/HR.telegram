package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginUrl{
    String url;
    String forward_text;
    String bot_username;
    boolean request_write_access;
}
