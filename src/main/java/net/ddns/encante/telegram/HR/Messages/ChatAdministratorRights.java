package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatAdministratorRights{
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
