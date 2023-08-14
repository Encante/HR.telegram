package net.ddns.encante.telegram.hr.menu.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuPattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    @Column(name = "name")
    String name;
    @Column(name = "upper_pattern")
    String upperPatternName;
    @Column(name = "text")
    String text;
    @Column(name = "rows")
    int rows;
    @Column(name = "cols")
    int cols;

    @OneToMany(mappedBy = "pattern")
    List<InlineMenuButton> buttons;
}
