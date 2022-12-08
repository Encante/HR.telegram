package net.ddns.encante.telegram.HR.TelegramObjects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SuccessfulPayment {
    String currency;
    Long total_amount;
    String invoice_payload;
    String shipping_option_id;
    OrderInfo order_info;
    String telegram_payment_charge_id;
    String provider_payment_charge_id;
}
