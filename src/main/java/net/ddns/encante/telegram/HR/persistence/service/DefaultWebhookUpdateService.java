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
        WebhookUpdateEntity updateEntity = convertWebhookUpdateObjToEntity(update);
//        check if update or user or chat or message exist already in db
        if (updateRepository.findWebhookUpdateEntityByEntityId(updateEntity.getUpdateId()) != null)
            updateEntity = updateRepository.findWebhookUpdateEntityByEntityId(updateEntity.getUpdateId());
        if (updateRepository.findMessageEntityByMessageId(updateEntity.getMessage().getMessageId()) != null)
            updateEntity.setMessage(updateRepository.findMessageEntityByMessageId(updateEntity.getMessage().getMessageId()));
        if (updateRepository.findChatEntityByChatId(updateEntity.getMessage().getChat().getChatId()) != null)
            updateEntity.getMessage().setChat(updateRepository.findChatEntityByChatId(updateEntity.getMessage().getChat().getChatId()));
        if (updateRepository.findUserEntityByUserId(updateEntity.getMessage().getFrom().getUserId()) != null)
            updateEntity.getMessage().setFrom(updateRepository.findUserEntityByUserId(updateEntity.getMessage().getChat().getChatId()));
        return convertWebhookUpdateEntityToObj(updateRepository.save(updateEntity));
    }
    @Override
    public boolean deleteWebhookUpdate(Long updateId){
        updateRepository.deleteById(updateId);
        return true;
    }
    @Override
    public WebhookUpdate getWebhookUpdateById(Long updateId){
        return convertWebhookUpdateEntityToObj(updateRepository.findById(updateId).orElseThrow(() -> new EntityNotFoundException("Webhook update not found")));
    }
    public UserEntity getUserEntityByUser_id(Long user_id){
        if (updateRepository.findUserEntityByUserId(user_id) != null){
            return updateRepository.findUserEntityByUserId(user_id);
        }
        else throw new EntityNotFoundException("No user with such id");
    }

    private WebhookUpdate convertWebhookUpdateEntityToObj(WebhookUpdateEntity entity){
        WebhookUpdate updateObj = new WebhookUpdate();
        updateObj.setUpdate_id(entity.getUpdateId());
        if (entity.getMessage() != null)
        updateObj.setMessage(convertMessageEntityToObj(entity.getMessage()));
        if (entity.getCallbackQuery() != null)
        updateObj.setCallback_query(converCallbackQueryEntityToObj(entity.getCallbackQuery()));
        return updateObj;
    }
    private CallbackQuery converCallbackQueryEntityToObj(CallbackQueryEntity queryEntity){
        CallbackQuery queryObj = new CallbackQuery();
        queryObj.setId(queryEntity.getCallbackId());
        queryObj.setFrom(convertUserEntityToObj(queryEntity.getFrom()));
        if (queryEntity.getMessage() != null)
        queryObj.setMessage(convertMessageEntityToObj(queryEntity.getMessage()));
        if (queryEntity.getCallbackId() != null)
        queryObj.setInline_message_id(queryEntity.getCallbackId());
        return queryObj;
    }
    private Message convertMessageEntityToObj(MessageEntity messageEntity){
        Message messageObj = new Message();
        messageObj.setMessage_id(messageEntity.getMessageId());
        if (messageEntity.getFrom() != null)
        messageObj.setFrom(convertUserEntityToObj(messageEntity.getFrom()));
        messageObj.setDate(messageEntity.getDate());
        messageObj.setChat(convertChatEntityToObj(messageEntity.getChat()));
        if (messageEntity.getReplyToMessage() != null)
        messageObj.setReply_to_message(convertMessageEntityToObj(messageEntity.getReplyToMessage()));
        if (messageEntity.getEditDate() != null)
        messageObj.setEdit_date(messageEntity.getEditDate());
        if (messageEntity.getText() != null)
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
        if (userEntity.getLastName() != null)
        userObj.setLast_name(userEntity.getLastName());
        if (userEntity.getUsername() != null)
        userObj.setUsername(userEntity.getUsername());
        return userObj;
    }
    private WebhookUpdateEntity convertWebhookUpdateObjToEntity(WebhookUpdate update){
        WebhookUpdateEntity entity = new WebhookUpdateEntity();
        entity.setUpdateId(update.getUpdate_id());
        if (update.getMessage() != null)
        entity.setMessage(convertMessageObjToEntity(update.getMessage()));
        if (update.getCallback_query() != null)
        entity.setCallbackQuery(convertCallbackQueryObjToEntity(update.getCallback_query()));
        return entity;
    }

    private MessageEntity convertMessageObjToEntity(Message message){
        MessageEntity entityMessage = new MessageEntity();
        entityMessage.setMessageId(message.getMessage_id());
        if (message.getFrom() != null)
        entityMessage.setFrom(convertUserObjToEntity(message.getFrom()));
        entityMessage.setDate(message.getDate());
        entityMessage.setChat(convertChatObjToEntity(message.getChat()));
        if (message.getReply_to_message()!= null)
        entityMessage.setReplyToMessage(convertMessageObjToEntity(message.getReply_to_message()));
        if (message.getEdit_date() != null)
        entityMessage.setEditDate(message.getEdit_date());
        if (message.getText() != null)
        entityMessage.setText(message.getText());
        return entityMessage;
    }
    private UserEntity convertUserObjToEntity(User user){
        UserEntity entityUser = new UserEntity();
        entityUser.setUserId(user.getId());
        entityUser.setBot(user.is_bot());
        entityUser.setFirstName(user.getFirst_name());
        if (user.getLast_name() != null)
            entityUser.setLastName(user.getLast_name());
        if (user.getUsername() != null)
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
        if (query.getMessage()!= null)
        entityQuery.setMessage(convertMessageObjToEntity(query.getMessage()));
        if (query.getInline_message_id() != null)
        entityQuery.setInlineMessageId(query.getInline_message_id());
        return entityQuery;
    }
}
