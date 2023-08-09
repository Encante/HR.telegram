package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Game {
    String title;
    String description;
    ArrayList<PhotoSize> photo;
    String text;
    ArrayList<MessageEntity> text_entities;
    Animation animation;
}
