package net.ddns.encante.telegram.HR.Hue;

import lombok.Data;

import java.util.ArrayList;

@Data
public class HueDevice {
    ArrayList<HueError> errors;
    ArrayList<HueDeviceGet> data;
}
