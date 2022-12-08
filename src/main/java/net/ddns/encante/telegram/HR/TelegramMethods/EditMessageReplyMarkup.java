package net.ddns.encante.telegram.HR.TelegramMethods;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.HR.TelegramObjects.CallbackQuery;
import net.ddns.encante.telegram.HR.TelegramObjects.SentMessage;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditMessageReplyMarkup extends EditMessage {
    public EditMessageReplyMarkup(SentMessage sentMessage) {
        super(sentMessage.getResult().getChat().getId(), sentMessage.getResult().getMessage_id());
    }

    public EditMessageReplyMarkup(CallbackQuery callback) {
        super(callback.getMessage().getChat().getId(), callback.getMessage().getMessage_id());
    }
}
