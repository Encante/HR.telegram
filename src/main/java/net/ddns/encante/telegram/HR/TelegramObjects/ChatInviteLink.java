package net.ddns.encante.telegram.HR.TelegramObjects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatInviteLink{
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
