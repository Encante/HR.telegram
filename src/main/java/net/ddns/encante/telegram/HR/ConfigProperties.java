package net.ddns.encante.telegram.HR;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "tokenz")
public class ConfigProperties {
    private String botToken;
}
