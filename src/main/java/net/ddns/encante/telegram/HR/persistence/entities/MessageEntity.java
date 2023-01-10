package net.ddns.encante.telegram.HR.persistence.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Messages")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    @Column(name = "message_id")
    Long messageId;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "from_key", referencedColumnName = "key_id")
    UserEntity from;
    @Column(name = "date")
    Long date;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_key",referencedColumnName = "key_id")
    ChatEntity chat;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "reply_to_message")
    MessageEntity replyToMessage;
    @Column(name = "edit_date")
    Long editDate;
    @Column(name = "text")
    String text;
}