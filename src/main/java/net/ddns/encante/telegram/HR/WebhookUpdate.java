package net.ddns.encante.telegram.HR;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

// klasa na updaty wysyłane webhookami
@Getter
public class WebhookUpdate {

        private Long update_id;
        Message message;
    }
    @Getter
    class Message {
        private Long message_id;
        private Integer message_thread_id;
        User from;
        Chat sender_chat;
        private Long date;
        Chat chat;
        User forward_from_chat;
        private Long forward_from_message_id;
        private String forward_signature;
        private String forward_sender_name;
        private Long forward_date;
        private boolean is_topic_message;
        private boolean is_automatic_forward;
        Message reply_to_message;
        User via_bot;
        private Long edit_date;
        private boolean has_protected_content;
        private String media_group_id;
        private String author_signature;
        private String text;
        ArrayList <MessageEntity> entities;
        Animation animation;
        Audio audio;
        Document document;
        ArrayList<PhotoSize> photo;
        Sticker sticker;
        Video video;
        VideoNote video_note;
        Voice voice;
        private String caption;
        ArrayList<MessageEntity> caption_entities;
        Contact contact;
        Dice dice;
        Game game;
        Poll poll;
        Venue venue;
        Location location;
        ArrayList<User> new_chat_members;
        User left_chat_member;
        private String new_chat_title;
        ArrayList<PhotoSize> new_chat_photo;
        private boolean delete_chat_photo;
        private boolean group_chat_created;
        private boolean supergroup_chat_created;
        private boolean channel_chat_created;
        MessageAutoDeleteTimerChanged message_auto_delete_timer_changed;
        private Long migrate_to_chat_id;
        private Long migrate_from_chat_id;
        Message pinned_message;
        Invoice invoice;
        SuccessfulPayment successful_payment;
        private String connected_website;
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
    class Chat {
        private Long id;
        private String first_name;
        private String last_name;
        private String type;
    }

    @Getter
    class User {
        private Long id;
        private boolean is_bot;
        private String first_name;
        private String last_name;
        private String language_code;
    }
