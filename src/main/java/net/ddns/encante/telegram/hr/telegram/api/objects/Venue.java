package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Venue{
    Location location;
    String title;
    String address;
    String foursquare_id;
    String foursquare_type;
    String google_place_id;
    String google_place_type;
}
