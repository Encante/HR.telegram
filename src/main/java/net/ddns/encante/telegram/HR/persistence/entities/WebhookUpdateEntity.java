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
    @Column(name = "reply_to_message_id")
    Long replyToMessageId;
    @Column(name = "reply_to_message_from_id")
    Long replyToMessageFromId;
    @Column(name = "reply_to_message_")
}