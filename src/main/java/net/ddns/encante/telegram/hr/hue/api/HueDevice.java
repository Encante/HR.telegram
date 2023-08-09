package net.ddns.encante.telegram.hr.hue.api;

import lombok.Data;

import java.util.ArrayList;

@Data
public class HueDevice {
    ArrayList<HueError> errors;
    ArrayList<HueDeviceGet> data;
}
