package net.ddns.encante.telegram.hr.hue.entity;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Table(name = "hue_authorization")
@Getter
public class HueAuthorization {
    private static final Logger log = LoggerFactory.getLogger(HueAuthorization.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    @Column(name = "app_id")
    String appId;
    @Column(name = "username")
    String username;
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
    @Column(name = "state")
    String state;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tokens_key", referencedColumnName = "key_id")
    HueTokens tokens;

//implemented null check in setters
    public void setKeyId(Long keyId) {
        if (keyId != null)
        this.keyId = keyId;
    }

    public void setAppId(String appId) {
        if (appId != null)
        this.appId = appId;
    }

    public void setClientId(String clientId) {
        if (clientId != null)
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        if (clientSecret != null)
        this.clientSecret = clientSecret;
    }

    public void setCallbackUrl(String callbackUrl) {
        if (callbackUrl != null)
        this.callbackUrl = callbackUrl;
    }

    public void setDisplayName(String displayName) {
        if (displayName != null)
        this.displayName = displayName;
    }

    public void setCode(String code) {
        if (code != null)
        this.code = code;
    }

    public void setState(String state) {
        if (state != null)
        this.state = state;
    }

    public void setTokens(HueTokens tokens) {
        if (tokens != null)
            this.tokens = tokens;
    }

    public void setUsername(String username) {
        if (tokens != null)
            this.username = username;
    }
}
