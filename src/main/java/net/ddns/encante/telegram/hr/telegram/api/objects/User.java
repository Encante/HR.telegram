package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id;
    Boolean is_bot;
    String first_name;
    String last_name;
    String username;
    String language_code;
    Boolean is_premium;
    Boolean added_to_attachment_menu;
    Boolean can_join_groups;
    Boolean can_read_all_group_messages;
    Boolean support_inline_queries;
}