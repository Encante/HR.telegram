package net.ddns.encante.telegram.hr.hue.api;

import lombok.Data;

@Data
public class HueError {
    Integer type;
    String address;
    String description;
}
