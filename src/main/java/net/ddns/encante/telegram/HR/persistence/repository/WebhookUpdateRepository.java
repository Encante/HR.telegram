package net.ddns.encante.telegram.HR.persistence.repository;

import net.ddns.encante.telegram.HR.persistence.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookUpdateRepository extends JpaRepository<WebhookUpdateEntity,Long> {
    @Query("SELECT u FROM UserEntity u WHERE u.userId = :userId")
    UserEntity findUserEntityByUserId(@Param("userId") Long userId);
    @Query("SELECT c FROM ChatEntity c WHERE c.chatId = :chatId")
    ChatEntity findChatEntityByChatId(@Param("chatId") Long chatId);
    @Query("SELECT m FROM MessageEntity m WHERE m.messageId = :messageId")
    MessageEntity findMessageEntityByMessageId(@Param("messageId") Long messageId);
    @Query("SELECT w FROM WebhookUpdateEntity w WHERE w.updateId = :updateId")
    WebhookUpdateEntity findWebhookUpdateEntityByEntityId(@Param("updateId") Long updateId);
    @Query("SELECT cb FROM CallbackQueryEntity cb WHERE cb.callbackId = :callbackId")
    CallbackQueryEntity findCallbackQueryEntityByCallbackId(@Param("callbackId") String callbackId);
}
