package net.ddns.encante.telegram.hr.events.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.ddns.encante.telegram.hr.events.EventType;

import javax.persistence.*;

@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Getter
@Setter
@Entity
@Table(name = "Events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long key_id;
    @Column(name = "date")
    Long date;
    @Column (name = "event_type")
    EventType type;
}
