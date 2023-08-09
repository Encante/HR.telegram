package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.hr.telegram.entity.User;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PreCheckoutQuery {
    String id;
    User from;
    String currency;
    Long total_amount;
    String invoice_payload;
    String shipping_option_id;
    OrderInfo order_info;
}
