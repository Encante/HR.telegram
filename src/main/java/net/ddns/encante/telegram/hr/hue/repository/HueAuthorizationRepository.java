package net.ddns.encante.telegram.hr.hue.repository;

import net.ddns.encante.telegram.hr.hue.entity.HueAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HueAuthorizationRepository extends JpaRepository<HueAuthorization, Long> {
    HueAuthorization findFirstByOrderByKeyIdAsc();
    HueAuthorization findByDisplayName(String displayName);
    HueAuthorization findByState(String state);
    HueAuthorization findByClientId (String clientId);
}
