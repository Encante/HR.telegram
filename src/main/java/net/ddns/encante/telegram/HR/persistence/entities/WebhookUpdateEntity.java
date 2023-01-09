package net.ddns.encante.telegram.HR.persistence.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "Webhook_Updates")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebhookUpdateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    @Column(name = "update_id")
    Long updateId;
    @Column(name = "message_id")
    Long messageId;
    @Column(name = "from_user_id")
    Long fromUserId;
    @Column(name = "is_from_bot")
    boolean isFromBot;
    @Column(name = "from_first_name")
    String fromFirstName;
    @Column(name = "from_last_name")
    String fromLastName;
    @Column(name = "from_username")
    String fromUsername;
    @Column(name = "date")
    Long date;
    @Column(name = "chat_id")
    Long chatId;
    @Column(name = "type")
    String chatType;
    @Column(name = "reply_to_message_id")
    Long replyToMessageId;
    @Column(name = "reply_to_message_from_id")
    Long replyToMessageFromId;
    @Column(name = "reply_to_message_")
}
