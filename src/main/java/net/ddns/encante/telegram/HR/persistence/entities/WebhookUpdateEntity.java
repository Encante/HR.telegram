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
    @Column(name = "callback_query")
    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "callback_querry_key", referencedColumnName = "key_id")
    CallbackQueryEntity callbackQuery;
}
