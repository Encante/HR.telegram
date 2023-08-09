package net.ddns.encante.telegram.hr.hue.api;

import lombok.Data;

@Data
public class HueResourceGet {
    String id;
    HueType type;
    String id_v1;
}
