package net.ddns.encante.telegram.hr.menu.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
@RequiredArgsConstructor
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
    @NonNull
    @Column(name = "chat_id")
    Long chatId;
    @Column(name = "message_id")
    Long messageId;
    @NonNull
    @Column(name = "last_sent_date")
    Long lastSentDate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "last_pattern_key", referencedColumnName = "key_id")
    MenuPattern lastPattern;
    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_pattern_key", referencedColumnName = "key_id")
    MenuPattern currentPattern;

}
