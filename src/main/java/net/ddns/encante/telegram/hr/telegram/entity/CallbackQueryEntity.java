package net.ddns.encante.telegram.hr.telegram.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Callback_Queries")
public class  CallbackQueryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    @Column(name = "callback_id")
    String callbackId;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "from_key", referencedColumnName = "key_id")
    UserEntity from;//many to one
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "message_key", referencedColumnName = "key_id")
    MessageEntity message;//many to one
    @Column(name = "inline_message_id")
    String inlineMessageId;
    @Column(name = "data")
    String data;
}