package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

// klasa na updaty wysyłane webhookami
//i nie tylko
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebhookUpdate {

        Long update_id;
        Message message;
        CallbackQuery callback_query;
}

