package net.ddns.encante.telegram.hr;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "tokenz")
public class ConfigProperties {
    private String botToken;
}
