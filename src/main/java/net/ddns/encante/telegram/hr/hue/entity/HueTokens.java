package net.ddns.encante.telegram.hr.hue.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "hue_tokens")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonNaming(PropertyNamingStrategy.)
public class HueTokens {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long key_id;

    @Column(name = "access_token")
    String access_token;
    @Column(name = "expires_in")
    Long expires_in;
    @Column(name = "refresh_token")
    String refresh_token;
    @Column(name = "token_type")
    String token_type;
    @Column(name = "created")
    Long created;
}
