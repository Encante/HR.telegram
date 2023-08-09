package net.ddns.encante.telegram.hr.telegram.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Callback_Queries")
public class CallbackQuery{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    @Column(name = "callback_id")
    String id;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "from_key", referencedColumnName = "key_id")
    User from; //sender
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "message_key", referencedColumnName = "key_id")
    Message message; //Optional. Message with the callback button that originated the query. Note that message content and message date will not be available if the message is too old
    @Column(name = "inline_message_id")
    String inline_message_id;
    @Column(name = "chat_instance")
    String chat_instance;
    @Column(name = "data")
    String data;
    @Column(name = "game_short_name")
    String game_short_name;
}