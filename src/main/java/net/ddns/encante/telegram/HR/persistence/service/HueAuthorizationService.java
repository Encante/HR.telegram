package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;

public interface HueAuthorizationService {
    HueAuthorizationEntity saveOrUpdateAuthorizationBasedOnClientId(HueAuthorizationEntity authorization);
    HueAuthorizationEntity getAuthorization();
    HueAuthorizationEntity getAuthorizationForDisplayName(String displayName);
    HueAuthorizationEntity getAuthorizationForState(String state);
}
