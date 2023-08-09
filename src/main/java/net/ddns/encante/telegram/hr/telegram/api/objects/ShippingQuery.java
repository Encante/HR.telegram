package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.hr.telegram.entity.User;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingQuery {
    String id;
    User from;
    String invoice_payload;
    ShippingAddress shipping_address;
}
