package net.ddns.encante.telegram.HR.Hue;

import lombok.Data;

@Data
public class HueError {
    Integer type;
    String address;
    String description;
}
