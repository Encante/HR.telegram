package net.ddns.encante.telegram.HR.persistence.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "Hue_Tokens")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HueTokensEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    @Column(name = "access_token")
    String accessToken;
    @Column(name = "expires_in")
    Long expiresIn;
    @Column(name = "refresh_token")
    String refreshToken;
    @Column(name = "token_type")
    String tokenType;
    @Column(name = "bearer")
    String bearer;
}
