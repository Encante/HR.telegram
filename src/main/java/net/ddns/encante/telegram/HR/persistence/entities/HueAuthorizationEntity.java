package net.ddns.encante.telegram.HR.persistence.entities;

import lombok.Getter;
import lombok.Setter;
import net.ddns.encante.telegram.HR.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Random;

@Entity
@Table(name = "hue_authorization")
@Getter
@Setter
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
            return "https://api.meethue.com/v2/oauth2/authorize?client_id=" + this.clientId+
            "&response_type=code&state=" + this.state;
        }
        else {
            log.warn("No clientId given or set");
            throw new RuntimeException("No clientId given!");}
    }
}
