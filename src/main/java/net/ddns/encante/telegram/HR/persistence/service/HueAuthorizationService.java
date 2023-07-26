package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import org.jetbrains.annotations.NotNull;

public interface HueAuthorizationService {
    HueAuthorizationEntity saveOrUpdateAuthorizationBasedOnClientId(HueAuthorizationEntity authorization);
    HueAuthorizationEntity getFirstAuthorization();
    HueAuthorizationEntity getAuthorizationForDisplayName(String displayName);
    HueAuthorizationEntity getAuthorizationForState(String state);
    void sendAuthorizationLink(@NotNull String displayName);
    void authenticateApp(@NotNull String state, @NotNull String code);
    String checkAndRefreshToken(@NotNull HueAuthorizationEntity authorization);
}
