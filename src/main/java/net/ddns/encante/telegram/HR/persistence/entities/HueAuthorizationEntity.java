package net.ddns.encante.telegram.HR.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Hue_Authorization")
@Getter
@Setter
public class HueAuthorizationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    @Column(name = "app_id")
    String appId;
    @Column(name = "client_id")
    String clientId;
    @Column(name = "client_secret")
    String clientSecret;
    @Column(name = "callback_url")
    String callbackUrl;
    @Column(name = "display_name")
    String displayName;
    @Column(name = "code")
    String code;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tokens_key", referencedColumnName = "key_id")
    HueTokensEntity tokens;
}
