package net.ddns.encante.telegram.HR.TelegramObjects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id;
    boolean is_bot;
    String first_name;
    String last_name;
    String username;
    String language_code;
    boolean is_premium;
    boolean added_to_attachment_menu;
    boolean can_join_groups;
    boolean can_read_all_group_messages;
    boolean support_inline_queries;
}