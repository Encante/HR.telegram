package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;
import net.ddns.encante.telegram.HR.data.WebhookUpdateEntity;
import net.ddns.encante.telegram.HR.persistence.repository.WebhookUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service("webhookUpdateService")
public class DefaultWebhookUpdateService implements WebhookUpdateService {
    @Autowired
    private WebhookUpdateRepository updateRepository;

    @Override
    public WebhookUpdate saveWebhookUpdate (WebhookUpdate update){
        WebhookUpdateEntity entity = populateWebhookUpdateEntity(update);
        return populateWebhookUpdate(updateRepository.save(update));
    }
    @Override
    public boolean deleteWebhookUpdate(Long updateId){
        updateRepository.deleteById(updateId);
        return true;
    }
    @Override
    public WebhookUpdate getWebhookUpdateById(Long updateId){
        return populateWebhookUpdate(updateRepository.findById(updateId).orElseThrow(() -> new EntityNotFoundException("Webhook update not found")));
    }

    private WebhookUpdate populateWebhookUpdate(final WebhookUpdate update){
        WebhookUpdate data = new WebhookUpdate();
        data.setUpdate_id(update.getUpdate_id());
        data.setMessage(update.getMessage());
        data.setCallback_query(update.getCallback_query());
        return data;
    }
    private WebhookUpdateEntity populateWebhookUpdateEntity(WebhookUpdate update){
        WebhookUpdateEntity entity = new WebhookUpdateEntity();
        entity.setUpdate_id(update.getUpdate_id());
        entity.setMessage(update.getMessage());
        entity.setCallback_query(update.getCallback_query());
        return entity;
    }
}
