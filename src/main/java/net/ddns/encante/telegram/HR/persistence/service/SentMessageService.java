package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.TelegramObjects.SentMessage;

public interface SentMessageService {
SentMessage saveSentMessage (SentMessage message);
boolean deleteSentMessage (final Long messageId);
SentMessage getSentMessageById(final Long messageId);
}
