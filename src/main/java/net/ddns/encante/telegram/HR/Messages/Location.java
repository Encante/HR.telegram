package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location{
    Float Longtitude;
    Float latitude;
    Float horizontal_accuracy;
    Long live_period;
    Long heading;
    Long proximity_alert_radius;
}