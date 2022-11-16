package net.ddns.encante.telegram.HR;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

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
@FieldDefaults(AccessLevel.PRIVATE)
class Video{

}