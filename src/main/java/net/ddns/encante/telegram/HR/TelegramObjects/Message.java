package net.ddns.encante.telegram.HR.TelegramObjects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

import java.util.ArrayList;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    Long message_id;
    Long message_thread_id;
    User from;
    Chat sender_chat;
    @NonNull
    Long date;
    @NonNull
    Chat chat;
    User forward_from_chat;
    Long forward_from_message_id;
    String forward_signature;
    String forward_sender_name;
    Long forward_date;
    Boolean is_topic_message;
    Boolean is_automatic_forward;
    Message reply_to_message;
    User via_bot;
    Long edit_date;
    Boolean has_protected_content;
    String media_group_id;
    String author_signature;
    String text;
    ArrayList<MessageEntity> entities;
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
    Boolean delete_chat_photo;
    Boolean group_chat_created;
    Boolean supergroup_chat_created;
    Boolean channel_chat_created;
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