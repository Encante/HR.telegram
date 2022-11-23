package net.ddns.encante.telegram.HR;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

import java.util.ArrayList;

// klasa na updaty wysyłane webhookami
//i nie tylko
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendMessageWithInlineKeyboard{
    @NonNull
    Long chat_id;
    Long message_thread_id;
    @NonNull
    String text;
    String parse_mode;
    ArrayList<MessageEntity> entities;
    boolean disable_web_page_preview;
    boolean disable_notification;
    boolean protect_content;
    Long reply_to_message_id;
    boolean allow_sending_without_reply;
    InlineKeyboardMarkup reply_markup;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendMessageWithReplyKeyboard{
    @NonNull
    Long chat_id;
    Long message_thread_id;
    @NonNull
    String text;
    String parse_mode;
    ArrayList<MessageEntity> entities;
    boolean disable_web_page_preview;
    boolean disable_notification;
    boolean protect_content;
    Long reply_to_message_id;
    boolean allow_sending_without_reply;
    ReplyKeyboardMarkup reply_markup;
}
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
    void sendUpdateToChatId(RemoteRequest request,Long chatId){
        if (message.getText()!= null) {
            request.sendMessageToChatId("New message! T: " + Utils.getCurrentDateTime()
                            + "  FROM: "
                            + message.getFrom().getFirst_name()
                            + " "
                            + message.getFrom().getLast_name()
                            + "  CHAT ID: "
                            + message.getChat().getId()
                            + "  CONTENT: "
                            + message.getText()
                    ,chatId);
        }
        else {
            request.sendMessageToChatId("New Message! T:"
                    + Utils.getCurrentDateTime()
                    +"but there's no text!"
                    +"  FROM: "
                    + message.getFrom().getFirst_name()
                    + " "
                    + message.getFrom().getLast_name()
                    + "  CHAT ID: "
                    + message.getChat().getId()
                    ,chatId);
        }
    }
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class User {
    Long id;
    boolean is_bot;
    String first_name;
    String last_name;
    String language_code;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Chat {
    Long id;
    String first_name;
    String last_name;
    String type;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Message {
    Long message_id;
    Long message_thread_id;
    User from;
    Chat sender_chat;
    Long date;
    Chat chat;
    User forward_from_chat;
    Long forward_from_message_id;
    String forward_signature;
    String forward_sender_name;
    Long forward_date;
    boolean is_topic_message;
    boolean is_automatic_forward;
    Message reply_to_message;
    User via_bot;
    Long edit_date;
    boolean has_protected_content;
    String media_group_id;
    String author_signature;
    String text;
    ArrayList <MessageEntity> entities;
    Animation animation;
    Audio audio;
    Document document;
    ArrayList<PhotoSize> photo;
    Sticker sticker;
    Video video;
    VideoNote video_note;
    Voice voice;
    String caption;
    ArrayList<MessageEntity> caption_entities;
    Contact contact;
    Dice dice;
    Game game;
    Poll poll;
    Venue venue;
    Location location;
    ArrayList<User> new_chat_members;
    User left_chat_member;
    String new_chat_title;
    ArrayList<PhotoSize> new_chat_photo;
    boolean delete_chat_photo;
    boolean group_chat_created;
    boolean supergroup_chat_created;
    boolean channel_chat_created;
    MessageAutoDeleteTimerChanged message_auto_delete_timer_changed;
    Long migrate_to_chat_id;
    Long migrate_from_chat_id;
    Message pinned_message;
    Invoice invoice;
    SuccessfulPayment successful_payment;
    String connected_website;
    PassportData passport_data;
    ProximityAlertTriggered proximity_alert_triggered;
    ForumTopicCreated forum_topic_created;
    ForumTopicClosed forum_topic_closed;
    ForumTopicReopened forum_topic_reopened;
    VideoChatScheduled video_chat_scheduled;
    VideoChatStarted video_chat_started;
    VideoChatEnded video_chat_ended;
    VideoChatParticipantsInvited video_chat_participants_invited;
    WebAppData web_app_data;
    InlineKeyboardMarkup reply_markup;
}

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MessageId{
    Long message_id;
}

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MessageEntity{
    String type;
    Long offset;
    Long lenght;
    String url;
    User user;
    String language;
    String custom_emoji_id;
}

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class PhotoSize{
    String file_id;
    String file_unique_id;
    Long width;
    Long height;
    Long file_size;
}

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Animation{
    String file_id;
    String file_unique_id;
    Long width;
    Long height;
    Long duration;
    PhotoSize thumb;
    String file_name;
    String mime_type;
    Long file_size;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Audio{
    String file_id;
    String file_unique_id;
    Long duration;
    String performer;
    String title;
    String file_name;
    String mime_type;
    Long file_size;
    PhotoSize thumb;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Document{
    String file_id;
    String file_unique_id;
    PhotoSize thumb;
    String file_name;
    String mime_type;
    Long file_size;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Video{
    String file_id;
    String file_unique_id;
    Long width;
    Long height;
    Long duration;
    PhotoSize thumb;
    String file_name;
    String mime_type;
    Long file_size;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class VideoNote{
    String file_id;
    String file_unique_id;
    Long lenght;
    Long duration;
    PhotoSize thumb;
    Long file_size;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Voice{
    String file_id;
    String file_unique_id;
    Long duration;
    String mime_type;
    Long file_size;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Contact{
    String phone_number;
    String first_name;
    String last_name;
    Long user_id;
    String vcard;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Dice{
    String emoji;
    Long value;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class PollOption{
    String text;
    Long voter_count;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class PollAnswer{
    String poll_id;
    User user;
    ArrayList<Long> option_ids;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Poll{
    String id;
    String question;
    ArrayList<PollOption> options;
    Long total_voter_count;
    boolean is_closed;
    boolean is_anonymous;
    String type;
    boolean allows_multiple_answers;
    Long correct_option_id;
    String explanation;
    ArrayList<MessageEntity> explanation_entities;
    Long open_period;
    Long close_date;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Location{
    Float Longtitude;
    Float latitude;
    Float horizontal_accuracy;
    Long live_period;
    Long heading;
    Long proximity_alert_radius;
}
@Getter @Setter
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
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class WebAppData{
    String data;
    String button_text;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ProximityAlertTriggered{
    User traveler;
    User watcher;
    Long distance;
    }
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MessageAutoDeleteTimerChanged{
    Long message_auto_delete_time;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ForumTopicCreated{
    String name;
    Long icon_color;
    String icon_custom_emoji_id;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ForumTopicClosed{}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ForumTopicReopened{}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class VideoChatScheduled{
    Long start_date;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class VideoChatStarted{}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class VideoChatEnded{
    Long duration;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class VideoChatParticipantsInvited{
    ArrayList<User> users;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserProfilePhotos{
    Long total_count;
    ArrayList<ArrayList<PhotoSize>> photos;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class File{
    String file_id;
    String file_unique_id;
    Long file_size;
    String file_path;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class WebAppInfo{
    String url;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ReplyKeyboardMarkup{
    ArrayList<ArrayList<KeyboardButton>> keyboard;
    boolean resize_keyboard;
    boolean one_time_keyboard;
    String input_field_placeholder;
    boolean selective;
    public ReplyKeyboardMarkup factory(int cols, int rows,String... args){
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            ArrayList<KeyboardButton> row;
            for (String arg : args) {
                names.add(arg);
            }
            for (int i = 0; i < rows; i++) {
                KeyboardButton
            }
        }

    }
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class KeyboardButton{
    String text;
    boolean request_contact;
    boolean request_location;
    KeyboardButtonPollType request_poll;
    WebAppInfo web_app;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class KeyboardButtonPollType{
    String text;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ReplyKeyboardRemove{
    boolean remove_keyboard;
    boolean selective;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class InlineKeyboardMarkup{
    ArrayList<ArrayList<InlineKeyboardButton>> inline_keyboard;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class InlineKeyboardButton{
    String text;
    String url;
    String callback_data;
    WebAppInfo web_app;
    LoginUrl login_url;
    String switch_inline_query;
    String switch_inline_query_current_chat;
    CallbackGame callback_game;
    boolean pay;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class LoginUrl{
    String url;
    String forward_text;
    String bot_username;
    boolean request_write_access;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class CallbackQuery{
    String id;
    User from;
    Message message;
    String inline_message_id;
    String chat_instance;
    String data;
    String game_short_name;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ForceReply{
    boolean force_reply;
    String input_field_placeholder;
    boolean selective;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatPhoto{
    String small_file_id;
    String small_file_unique_id;
    String big_file_id;
    String big_file_unique_id;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatInviteLink{
    String invite_link;
    User creator;
    boolean creates_join_request;
    boolean is_primary;
    boolean is_revoked;
    String name;
    Long expire_date;
    Long member_limit;
    Long pending_join_request_count;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatAdministratorRights{
    boolean is_anonymous;
    boolean can_manage_chat;
    boolean can_delete_messages;
    boolean can_manage_video_chats;
    boolean can_restrict_members;
    boolean can_promote_members;
    boolean can_change_info;
    boolean can_invite_users;
    boolean can_post_messages;
    boolean can_edit_messages;
    boolean can_pin_messages;
    boolean can_manage_topics;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatMember{}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatMemberOwner {
    String status;
    User user;
    boolean is_anonymous;
    String custom_title;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatMemberAdministrator{
    String status;
    User user;
    boolean can_be_edited;
    boolean is_anonymous;
    boolean can_manage_chat;
    boolean can_delete_messages;
    boolean can_manage_video_chats;
    boolean can_restrict_members;
    boolean can_promote_members;
    boolean can_change_info;
    boolean can_invite_users;
    boolean can_post_messages;
    boolean can_edit_messages;
    boolean can_pin_messages;
    boolean can_manage_topics;
    String custom_title;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatMemberMember{
    String status;
    User user;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatMemberRestricted {
    String status;
    User user;
    boolean is_member;
    boolean can_change_info;
    boolean can_invite_users;
    boolean can_pin_messages;
    boolean can_manage_topics;
    boolean can_send_messages;
    boolean can_send_media_messages;
    boolean can_send_polls;
    boolean can_send_other_messages;
    boolean can_add_web_page_previews;
    Long until_date;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatMemberLeft{
    String status;
    User user;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatMemberBanned{
    String status;
    User user;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatMemberUpdated{
    Chat chat;
    User from;
    Long date;
    ChatMember old_chat_member;
    ChatMember new_chat_member;
    ChatInviteLink invite_link;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatJoinRequest{
    Chat chat;
    User from;
    Long date;
    String bio;
    ChatInviteLink invite_link;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatPermissions{
    boolean can_send_messages;
    boolean can_send_media_messages;
    boolean can_send_polls;
    boolean can_send_other_messages;
    boolean can_add_web_page_previews;
    boolean can_change_info;
    boolean can_invite_users;
    boolean can_pin_messages;
    boolean can_manage_topics;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatLocation{
    Location location;
    String adress;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ForumTopic{
    Long message_thread_id;
    String name;
    Long icon_color;
    String icon_custom_emoji_id;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class BotCommand{
    String command;
    String description;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class BotCommandScope{}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class BotCommandScopeDefault{
    String type;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class BotCommandScopeAllPrivateChats{
    String type;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class BotCommandScopeAllGroupChats{
    String type;
}
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class BotCommandScopeAllChatAdministrators{
    String type;
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
