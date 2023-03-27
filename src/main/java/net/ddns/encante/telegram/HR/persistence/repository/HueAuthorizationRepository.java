package net.ddns.encante.telegram.HR.persistence.repository;

import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HueAuthorizationRepository extends JpaRepository<HueAuthorizationEntity, Long> {
    HueAuthorizationEntity findFirstByOrderByKeyIdAsc();
    HueAuthorizationEntity findByDisplayName(String displayName);
    HueAuthorizationEntity findByState(String state);
    HueAuthorizationEntity findByClientId (String clientId);
}
