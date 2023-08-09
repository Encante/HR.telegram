package net.ddns.encante.telegram.hr.hue.api;

import lombok.Data;

@Data
public class HueFault {
    public String faultstring;
    public HueDetail detail;
}