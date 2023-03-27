package net.ddns.encante.telegram.HR.persistence.entities;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Random;

@Entity
@Table(name = "hue_authorization")
@Getter
public class HueAuthorizationEntity {
    private static final Logger log = LoggerFactory.getLogger(HueAuthorizationEntity.class);
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
    @Column(name = "state")
    String state;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tokens_key", referencedColumnName = "key_id")
    HueTokensEntity tokens;

    public String generateAuthorizationLink(){
        if (this.clientId!= null) {
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();

            this.state = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            this.code=null;
            this.tokens = null;
            return "https://api.meethue.com/v2/oauth2/authorize?client_id=" + this.clientId+
            "&response_type=code&state=" + this.state;
        }
        else {
            log.warn("No clientId given or set");
            throw new RuntimeException("No clientId given!");}
    }
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

    public void setTokens(HueTokensEntity tokens) {
        if (tokens != null)
        this.tokens = tokens;
    }
}
