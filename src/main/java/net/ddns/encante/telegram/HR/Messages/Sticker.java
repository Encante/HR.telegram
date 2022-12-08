package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sticker {
    String file_id;
    String file_unique_id;
    String type;
    Long width;
    Long height;
    boolean is_animated;
    boolean is_video;
    PhotoSize thumb;
    String emoji;
    String set_name;
    File premium_animation;
    MaskPosition mask_position;
    String custom_emoji_id;
    Long file_size;
}
