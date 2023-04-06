package net.ddns.encante.telegram.HR.Hue;

import lombok.Data;

@Data
public class HueProductData {
    String model_id;
    String manufacturer_name;
    String product_name;
    String product_archetype;
    Boolean certified;
    String software_version;
    String hardware_platform_type;
}
