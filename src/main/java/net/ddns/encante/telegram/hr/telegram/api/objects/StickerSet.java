package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StickerSet {
    String name;
    String title;
    String sticker_type;
    boolean is_animated;
    boolean is_video;
    ArrayList<Sticker> stickers;
    PhotoSize thumb;
}
