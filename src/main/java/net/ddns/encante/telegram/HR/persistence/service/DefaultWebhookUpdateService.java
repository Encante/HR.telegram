package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.TelegramObjects.*;
import net.ddns.encante.telegram.HR.persistence.entities.*;
import net.ddns.encante.telegram.HR.persistence.entities.MessageEntity;
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
        WebhookUpdateEntity entity = convertWebhookUpdateObjToEntity(update);
        return updateRepository.save(update);
    }
    @Override
    public boolean deleteWebhookUpdate(Long updateId){
        updateRepository.deleteById(updateId);
        return true;
    }
    @Override
    public WebhookUpdate getWebhookUpdateById(Long updateId){
        return updateRepository.findById(updateId).orElseThrow(() -> new EntityNotFoundException("Webhook update not found"));
    }

    private WebhookUpdate convertWebhookUpdateEntityToObj(WebhookUpdateEntity entity){
        WebhookUpdate updateObj = new WebhookUpdate();
        updateObj.setUpdate_id(entity.getUpdateId());
        updateObj.setMessage(convertMessageEntityToObj(entity.getMessage()));
        updateObj.setCallback_query(converCallbackQueryEntityToObj(entity.getCallbackQuery()));
        return updateObj;
    }
    private CallbackQuery converCallbackQueryEntityToObj(CallbackQueryEntity queryEntity){
        CallbackQuery queryObj = new CallbackQuery();
        queryObj.setId(queryEntity.getCallbackId());
        queryObj.setFrom(convertUserEntityToObj(queryEntity.getFrom()));
        queryObj.setMessage(convertMessageEntityToObj(queryEntity.getMessage()));
        queryObj.setInline_message_id(queryEntity.getCallbackId());
        return queryObj;
    }
    private Message convertMessageEntityToObj(MessageEntity messageEntity){
        Message messageObj = new Message();
        messageObj.setMessage_id(messageEntity.getMessageId());
        messageObj.setFrom(convertUserEntityToObj(messageEntity.getFrom()));
        messageObj.setDate(messageEntity.getDate());
        messageObj.setChat(convertChatEntityToObj(messageEntity.getChat()));
        messageObj.setReply_to_message(convertMessageEntityToObj(messageEntity.getReplyToMessage()));
        messageObj.setEdit_date(messageEntity.getEditDate());
        messageObj.setText(messageEntity.getText());
        return messageObj;
    }
    private Chat convertChatEntityToObj(ChatEntity chatEntity){
        Chat chatObj = new Chat();
        chatObj.setId(chatEntity.getChatId());
        chatObj.setType(chatEntity.getType());
        return chatObj;
    }
    private User convertUserEntityToObj(UserEntity userEntity){
        User userObj = new User();
        userObj.setId(userEntity.getUserId());
        userObj.set_bot(userEntity.isBot());
        userObj.setFirst_name(userEntity.getFirstName());
        userObj.setLast_name(userEntity.getLastName());
        userObj.setUsername(userEntity.getUsername());
        return userObj;
    }
    private WebhookUpdateEntity convertWebhookUpdateObjToEntity(WebhookUpdate update){
        WebhookUpdateEntity entity = new WebhookUpdateEntity();
        entity.setUpdateId(update.getUpdate_id());
        entity.setMessage(convertMessageObjToEntity(update.getMessage()));
        entity.setCallbackQuery(convertCallbackQueryObjToEntity(update.getCallback_query()));
        return entity;
    }

    private MessageEntity convertMessageObjToEntity(Message message){
        MessageEntity entityMessage = new MessageEntity();
        entityMessage.setMessageId(message.getMessage_id());
        entityMessage.setFrom(convertUserObjToEntity(message.getFrom()));
        entityMessage.setDate(message.getDate());
        entityMessage.setChat(convertChatObjToEntity(message.getChat()));
        entityMessage.setReplyToMessage(convertMessageObjToEntity(message.getReply_to_message()));
        entityMessage.setEditDate(message.getEdit_date());
        entityMessage.setText(message.getText());
        return entityMessage;
    }
    private UserEntity convertUserObjToEntity(User user){
        UserEntity entityUser = new UserEntity();
        entityUser.setUserId(user.getId());
        entityUser.setBot(user.is_bot());
        entityUser.setFirstName(user.getFirst_name());
        entityUser.setLastName(user.getLast_name());
        entityUser.setUsername(user.getUsername());
        return entityUser;
    }
    private ChatEntity convertChatObjToEntity(Chat chat){
        ChatEntity entityChat = new ChatEntity();
        entityChat.setChatId(chat.getId());
        entityChat.setType(chat.getType());
        return entityChat;
    }
    private CallbackQueryEntity convertCallbackQueryObjToEntity(CallbackQuery query){
        CallbackQueryEntity entityQuery = new CallbackQueryEntity();
        entityQuery.setCallbackId(query.getId());
        entityQuery.setFrom(convertUserObjToEntity(query.getFrom()));
        entityQuery.setMessage(query.getMessage());
        entityQuery.setInlineMessageId(query.getInline_message_id());
    }
}
