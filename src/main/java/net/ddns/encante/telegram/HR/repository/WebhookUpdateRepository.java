package net.ddns.encante.telegram.HR.repository;

import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookUpdateRepository extends JpaRepository<WebhookUpdate,Long> {
}
