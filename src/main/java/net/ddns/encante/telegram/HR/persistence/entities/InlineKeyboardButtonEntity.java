package net.ddns.encante.telegram.HR.persistence.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Inline_Keyboard_Buttons")
public class InlineKeyboardButtonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    @ManyToOne
    @JoinColumn(name = "inline_keyboard_markup_row_entity_id",nullable = false)
    InlineKeyboardMarkupRowEntity inlineKeyboardMarkupRowEntity;

    @Column(name = "text")
    String text;
    @Column(name = "callback_data")
    String callbackData;//optional
}
