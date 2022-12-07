package net.ddns.encante.telegram.HR.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMemberUpdated{
    Chat chat;
    User from;
    Long date;
    ChatMember old_chat_member;
    ChatMember new_chat_member;
    ChatInviteLink invite_link;
}