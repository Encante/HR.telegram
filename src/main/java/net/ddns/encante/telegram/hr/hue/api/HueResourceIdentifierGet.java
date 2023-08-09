package net.ddns.encante.telegram.hr.hue.api;

import lombok.Data;

@Data
public class HueResourceIdentifierGet {
    String rid;
    HueType type;
}
