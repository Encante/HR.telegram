package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collections;

// klasa na updaty wysyłane webhookami
//i nie tylko
@Getter @Setter
public class WebhookUpdate {

        Long update_id;
        Message message;
        CallbackQuery callback_query;
//        METODY
    void printUpdateToConsole(){
    if (message.getText()!=null){
        System.out.println("update id: " + getUpdate_id());
        System.out.println("chat id: " + message.getChat().getId());
        System.out.println("message id: " + message.getMessage_id());
        System.out.println("message: " + message.getText());
        System.out.println("From: " + message.getFrom().getFirst_name());
        if (message.getFrom().getLast_name() != null)
            System.out.println(message.getFrom().getLast_name());
        System.out.println("Is bot: " + message.getFrom().is_bot());
        }
    else {
        System.out.println("Not a text message!");
        System.out.println("update id: " + getUpdate_id());
        System.out.println("chat id: " + message.getChat().getId());
        System.out.println("message id: " + message.getMessage_id());
        System.out.println("From: " + message.getFrom().getFirst_name());
        if (message.getFrom().getLast_name() != null)
            System.out.println(message.getFrom().getLast_name());
        System.out.println("Is bot: " + message.getFrom().is_bot());
        }
    }
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class BotCommandScopeChat{
    String type;
    Long chat_id;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class BotCommandScopeAdministrators{
    String type;
    Long chat_id;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class BotCommandScopeChatMember{
    String type;
    Long chat_id;
    Long user_id;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MenuButton{}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MenuButtonCommands{
    String type;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MenuButtonWebApp{
    String type;
    String text;
    WebAppInfo web_app;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MenuButtonDefault{
    String type;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ResponseParameters {
    Long migrate_to_chat_id;
    Long retry_after;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class InputMedia{}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class InputMediaPhoto{
    String type;
    String media;
    String caption;
    String parse_mode;
    ArrayList<MessageEntity> caption_entities;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class InputMediaVideo{
    String type;
    String media;
    String thumb;
    String caption;
    String parse_mode;
    ArrayList<MessageEntity> caption_entities;
    Long width;
    Long height;
    Long duration;
    boolean supports_streaming;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class InputMediaAnimation{
    String type;
    String media;
    String thumb;
    String caption;
    String parse_mode;
    ArrayList<MessageEntity> caption_entities;
    Long width;
    Long height;
    Long duration;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class InputMediaAudio{
    String type;
    String media;
    String thumb;
    String caption;
    String parse_mode;
    ArrayList<MessageEntity> caption_entities;
    Long duration;
    String performer;
    String title;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class InputMediaDocument{
    String type;
    String media;
    String thumb;
    String caption;
    String parse_mode;
    ArrayList<MessageEntity> caption_entities;
    boolean disable_content_type_detection;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Sticker{
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
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class StickerSet{
    String name;
    String title;
    String sticker_type;
    boolean is_animated;
    boolean is_video;
    ArrayList<Sticker> stickers;
    PhotoSize thumb;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MaskPosition{
    String point;
    Float x_shift;
    Float y_shift;
    Float scale;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class LabeledPrice{
    String label;
    Long amount;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Invoice{
    String type;
    String description;
    String start_parameter;
    String currency;
    Long total_amount;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ShippingAddress{
    String country_code;
    String state;
    String city;
    String street_line1;
    String street_line2;
    String post_code;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class OrderInfo{
    String name;
    String phone_number;
    String email;
    ShippingAddress shipping_address;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ShippingOption{
    String id;
    String title;
    ArrayList<LabeledPrice> prices;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SuccessfulPayment{
    String currency;
    Long total_amount;
    String invoice_payload;
    String shipping_option_id;
    OrderInfo order_info;
    String telegram_payment_charge_id;
    String provider_payment_charge_id;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ShippingQuery{
    String id;
    User from;
    String invoice_payload;
    ShippingAddress shipping_address;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class PreCheckoutQuery{
    String id;
    User from;
    String currency;
    Long total_amount;
    String invoice_payload;
    String shipping_option_id;
    OrderInfo order_info;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class PassportData{
    ArrayList<EncryptedPassportElement> data;
    EncryptedCredentials credentials;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class PassportFile{
    String file_id;
    String file_unique_id;
    Long file_size;
    Long file_date;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class EncryptedPassportElement{
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
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class EncryptedCredentials{
    String data;
    String hash;
    String secret;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Game{
    String title;
    String description;
    ArrayList<PhotoSize> photo;
    String text;
    ArrayList<MessageEntity> text_entities;
    Animation animation;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class CallbackGame{}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class GameHighScore{
    Long position;
    User user;
    Long score;
}
