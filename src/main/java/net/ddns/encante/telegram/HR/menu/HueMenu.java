package net.ddns.encante.telegram.HR.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HueMenu extends Menu {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "last_pattern_key", referencedColumnName = "key_id")
    MenuPattern lastPattern;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_pattern_key", referencedColumnName = "key_id")
    MenuPattern currentPattern;
}
