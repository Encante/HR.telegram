package net.ddns.encante.telegram.HR.persistence.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "Inline_Keyboard_Markups")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InlineKeyboardMarkupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "key_id")
    Long keyId;

    @OneToMany (mappedBy = "inlineKeyboardMarkupEntity")
    ArrayList<InlineKeyboardMarkupRowEntity> inlineKeyboardMarkupRowEntities;
}
