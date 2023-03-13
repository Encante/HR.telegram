package net.ddns.encante.telegram.HR.persistence.repository;

import net.ddns.encante.telegram.HR.persistence.entities.HueTokensEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HueTokensRepository extends JpaRepository<HueTokensEntity,Long> {
    HueTokensEntity findFirstByOrderByKeyIdAsc();
    HueTokensEntity findByBearer(String bearer);
}
