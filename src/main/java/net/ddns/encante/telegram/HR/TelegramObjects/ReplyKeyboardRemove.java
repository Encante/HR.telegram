package net.ddns.encante.telegram.HR.TelegramObjects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyKeyboardRemove{
    boolean remove_keyboard=true;
    boolean selective;
}
