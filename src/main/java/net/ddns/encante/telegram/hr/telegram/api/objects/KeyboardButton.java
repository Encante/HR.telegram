package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyboardButton{
    @NonNull
    String text;
    boolean request_contact;
    boolean request_location;
    KeyboardButtonPollType request_poll;
    WebAppInfo web_app;
    public KeyboardButton(@NotNull String text){
        this.text = text;
    }
}
