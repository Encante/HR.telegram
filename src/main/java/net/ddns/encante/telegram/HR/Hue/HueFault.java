package net.ddns.encante.telegram.HR.Hue;

import lombok.Data;

@Data
public class HueFault {
    public String faultstring;
    public HueDetail detail;
}