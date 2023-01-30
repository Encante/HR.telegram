package net.ddns.encante.telegram.HR.persistence.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "Users")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    @Column(name = "user_id")
    Long userId;
    @Column(name = "is_bot")
    Boolean isBot;
    @Column(name = "first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    @Column(name = "username")
    String username;
}
