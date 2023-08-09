package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Poll{
    String id;
    String question;
    ArrayList<PollOption> options;
    Long total_voter_count;
    boolean is_closed;
    boolean is_anonymous;
    String type;
    boolean allows_multiple_answers;
    Long correct_option_id;
    String explanation;
    ArrayList<MessageEntity> explanation_entities;
    Long open_period;
    Long close_date;
}