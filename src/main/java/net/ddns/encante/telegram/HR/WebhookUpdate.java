package net.ddns.encante.telegram.HR;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

// klasa na updaty wysyłane webhookami
@Getter
public class WebhookUpdate {

        Long update_id;
        Message message;
    }
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class User {
    Long id;
    boolean is_bot;
    String first_name;
    String last_name;
    String language_code;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Chat {
    Long id;
    String first_name;
    String last_name;
    String type;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Message {
    Long message_id;
    Integer message_thread_id;
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
    VideoChatPaticipantsInvited video_chat_participants_invited;
    WebAppData web_app_data;
    InlineKeyboardMarkup reply_markup;
}

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MessageId{
    Long message_id;
}

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MessageEntity{
    String type;
    Integer offset;
    Integer lenght;
    String url;
    User user;
    String language;
    String custom_emoji_id;
}

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class PhotoSize{
    String file_id;
    String file_unique_id;
    Integer width;
    Integer height;
    Long file_size;
}

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Animation{
    String file_id;
    String file_unique_id;
    Integer width;
    Integer height;
    Integer duration;
    PhotoSize thumb;
    String file_name;
    String mime_type;
    Long file_size;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Audio{
    String file_id;
    String file_unique_id;
    Integer duration;
    String performer;
    String title;
    String file_name;
    String mime_type;
    Long file_size;
    PhotoSize thumb;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Document{
    String file_id;
    String file_unique_id;
    PhotoSize thumb;
    String file_name;
    String mime_type;
    Long file_size;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Video{
    String file_id;
    String file_unique_id;
    Integer width;
    Integer height;
    Integer duration;
    PhotoSize thumb;
    String file_name;
    String mime_type;
    Long file_size;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class VideoNote{
    String file_id;
    String file_unique_id;
    Integer lenght;
    Integer duration;
    PhotoSize thumb;
    Long file_size;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Voice{
    String file_id;
    String file_unique_id;
    Integer duration;
    String mime_type;
    Long file_size;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Contact{
    String phone_number;
    String first_name;
    String last_name;
    Long user_id;
    String vcard;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Dice{
    String emoji;
    Integer value;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class PollOption{
    String text;
    Integer voter_count;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class PollAnswer{
    String poll_id;
    User user;
    ArrayList<Integer> option_ids;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Poll{
    String id;
    String question;
    ArrayList<PollOption> options;
    Integer total_voter_count;
    boolean is_closed;
    boolean is_anonymous;
    String type;
    boolean allows_multiple_answers;
    Integer correct_option_id;
    String explanation;
    ArrayList<MessageEntity> explanation_entities;
    Integer open_period;
    Long close_date;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class Location{
    Float longtitude;
    Float latitude;
    Float horizontal_accuracy;
    Integer live_period;
    Integer heading;
    Integer proximity_alert_radius;
}
@Getter
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
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class WebAppData{
    String data;
    String button_text;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ProximityAlertTriggered{
    User traveler;
    User watcher;
    Integer distance;
    }
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class MessageAutoDeleteTimerChanged{
    Integer message_auto_delete_time;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ForumTopicCreated{
    String name;
    Integer icon_color;
    String icon_custom_emoji_id;
}
class ForumTopicClosed;
class ForumTopicReopened;
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class VideoChatScheduled{
    Long start_date;
}
class VideoChatStarted;
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class VideoChatEnded{
    Integer duration;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class VideoChatParticipantsInvited{
    ArrayList<User> users;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserProfilePhotos{
    Integer total_count;
    ArrayList<ArrayList<PhotoSize>> photos;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class File{
    String file_id;
    String file_unique_id;
    Long file_size;
    String file_path;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class WebAppInfo{
    String url;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ReplyKeyboardMarkup{
    ArrayList<ArrayList<KeyboardButton>> keyboard;
    boolean resize_keyboard;
    boolean one_time_keyboard;
    String input_field_placeholder;
    boolean selective;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class KeyboardButton{
    String text;
    boolean request_contact;
    boolean request_location;
    KeyboardButtonPollType request_poll;
    WebAppInfo web_app;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class KeyboardButtonPollType{
    String text;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ReplyKeyboardRemove{
    boolean remove_keyboard;
    boolean selective;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class InlineKeyboardMarkup{
    ArrayList<ArrayList<InlineKeyboardButton>> inline_keyboard;
}
@Getter
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
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class LoginUrl{
    String url;
    String forward_text;
    String bot_username;
    boolean request_write_access;
}
@Getter
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
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ForceReply{
    boolean force_reply;
    String input_field_placeholder;
    boolean selective;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatPhoto{
    String small_file_id;
    String small_file_unique_id;
    String big_file_id;
    String big_file_unique_id;
}
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
class ChatInviteLink{
    String invite_link;
    User creator;
    boolean creates_join_request;
    boolean is_primary;
    boolean is_revoked;
    String name;
    Long expire_date;
    Integer member_limit;
    Integer pending_join_request_count;
}
@Getter
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
class ChatMember;
