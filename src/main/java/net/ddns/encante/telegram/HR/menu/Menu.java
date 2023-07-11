package net.ddns.encante.telegram.HR.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Menu")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;
    @Column(name = "chat_id")
    Long chatId;
    @Column(name = "message_id")
    Long messageId;
    @Column(name = "last_sent_date")
    Long lastSentDate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "last_pattern_key", referencedColumnName = "key_id")
    MenuPattern lastPattern;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_pattern_key", referencedColumnName = "key_id")
    MenuPattern currentPattern;
}
