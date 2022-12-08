package net.ddns.encante.telegram.HR.TelegramObjects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMemberRestricted {
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
