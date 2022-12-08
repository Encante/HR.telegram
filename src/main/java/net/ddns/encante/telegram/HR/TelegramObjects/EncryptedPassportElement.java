package net.ddns.encante.telegram.HR.TelegramObjects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EncryptedPassportElement {
    String type;
    String data;
    String phone_number;
    String email;
    ArrayList<PassportFile> files;
    PassportFile front_inside;
    PassportFile reverse_side;
    PassportFile selfie;
    ArrayList<PassportFile> translation;
    String hash;
}
