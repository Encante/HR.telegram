package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.Hue.HueTokens;
import net.ddns.encante.telegram.HR.persistence.entities.HueTokensEntity;

public interface HueTokensService {
    HueTokens saveHueTokens(HueTokens hueTokens);
    HueTokens getTokens();
    HueTokens getBearerTokens(String bearer);
}
