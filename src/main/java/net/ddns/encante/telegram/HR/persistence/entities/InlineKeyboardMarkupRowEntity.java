package net.ddns.encante.telegram.HR.persistence.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Inline_Keyboard_Markup_Rows")
public class InlineKeyboardMarkupRowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "key_id")
    Long keyId;

    @ManyToOne
    @JoinColumn(name = "inline_keyboard_markup_entity_id", nullable = false)
    InlineKeyboardMarkupEntity inlineKeyboardMarkupEntity;
    @OneToMany (mappedBy = "inlineKeyboardMarkupRowEntity")
    ArrayList<InlineKeyboardButtonEntity> inlineKeyboardButtonEntities;
}
