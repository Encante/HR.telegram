package net.ddns.encante.telegram.HR.Hue;

import lombok.Data;

import java.util.ArrayList;
@Data
public class HueDeviceGet {
    public String id;
    public String id_v1;
    public HueProductData product_data;
    public HueMetadata metadata;
    public HueIdentify identify;
    public ArrayList<HueResourceIdentifierGet> services;
    public HueType type;
}
