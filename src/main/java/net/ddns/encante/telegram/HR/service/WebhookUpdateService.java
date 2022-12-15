package net.ddns.encante.telegram.HR.service;

import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;

public interface WebhookUpdateService {
    WebhookUpdate saveWebhookUpdate(WebhookUpdate update);
    boolean deleteWebhookUpdate(final Long updateId);
    WebhookUpdate getWebhookUpdateById(final Long updateId);
}
