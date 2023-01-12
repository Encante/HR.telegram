package net.ddns.encante.telegram.HR.persistence.repository;

import net.ddns.encante.telegram.HR.persistence.entities.ChatEntity;
import net.ddns.encante.telegram.HR.persistence.entities.MessageEntity;
import net.ddns.encante.telegram.HR.persistence.entities.UserEntity;
import net.ddns.encante.telegram.HR.persistence.entities.WebhookUpdateEntity;
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
}
