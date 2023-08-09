package net.ddns.encante.telegram.hr.telegram.service;

import net.ddns.encante.telegram.hr.telegram.api.objects.*;
import net.ddns.encante.telegram.hr.telegram.entity.MessageEntity;
import net.ddns.encante.telegram.hr.telegram.entity.*;
import net.ddns.encante.telegram.hr.telegram.repository.WebhookUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service("webhookUpdateService")
public class WebhookUpdateService {
    @Autowired
    private WebhookUpdateRepository updateRepository;

    
    public WebhookUpdate saveWebhookUpdate (WebhookUpdate update){
        WebhookUpdateEntity updateEntity = convertWebhookUpdateObjToEntity(update);
//        check if update or user or chat or message exist already in db
        updateEntity = checkDbForExistingWebhookUpdates(updateEntity);
        return convertWebhookUpdateEntityToObj(updateRepository.save(updateEntity));
    }
    
    public boolean deleteWebhookUpdateById(Long updateId){
        updateRepository.deleteById(updateId);
        return true;
    }
    
    public WebhookUpdate getWebhookUpdateById(Long updateId){
        return convertWebhookUpdateEntityToObj(updateRepository.findById(updateId).orElseThrow(() -> new EntityNotFoundException("Webhook update not found")));
    }
//
//    CHECK DB FOR EXISTING RECORDS METHODS
//
    private WebhookUpdateEntity checkDbForExistingWebhookUpdates (WebhookUpdateEntity update){
//        if there is update with this id in base simply return existing record
        if (updateRepository.findWebhookUpdateEntityByEntityId(update.getUpdateId()) != null)
            return updateRepository.findWebhookUpdateEntityByEntityId(update.getUpdateId());
//        if not check other parts of update - message
        else {
            if (update.getMessage() != null) update.setMessage(checkDbForExistingMessages(update.getMessage()));
//        if not check other parts of update - callback query
            if (update.getCallbackQuery() != null) update.setCallbackQuery(checkDbForExistingCallbackQueries(update.getCallbackQuery()));
            return update;
        }
    }
    private MessageEntity checkDbForExistingMessages (MessageEntity messageEntity){
        if (updateRepository.findMessageEntityByMessageId(messageEntity.getMessageId()) != null){
            return updateRepository.findMessageEntityByMessageId(messageEntity.getMessageId());
        }
        else {
            if (messageEntity.getFrom() != null)
                messageEntity.setFrom(checkDbForExistingUsers(messageEntity.getFrom()));
            if (messageEntity.getReplyToMessage() != null)
                messageEntity.setReplyToMessage(checkDbForExistingMessages(messageEntity.getReplyToMessage()));
            messageEntity.setFrom(checkDbForExistingUsers(messageEntity.getFrom()));
            messageEntity.setChat(checkDbForExistingChats(messageEntity.getChat()));
            return messageEntity;
        }
    }
    private ChatEntity checkDbForExistingChats (ChatEntity chat){
        if (updateRepository.findChatEntityByChatId(chat.getChatId()) != null)
            return updateRepository.findChatEntityByChatId(chat.getChatId());
        else return chat;
    }
    UserEntity checkDbForExistingUsers (UserEntity user){
        if (updateRepository.findUserEntityByUserId(user.getUserId()) != null)
            return updateRepository.findUserEntityByUserId(user.getUserId());
        else return user;
    }

    private CallbackQueryEntity checkDbForExistingCallbackQueries (CallbackQueryEntity cbq){
        if (updateRepository.findCallbackQueryEntityByCallbackId(cbq.getCallbackId()) != null)
            return updateRepository.findCallbackQueryEntityByCallbackId(cbq.getCallbackId());
        else {
            if (cbq.getMessage() != null) cbq.setMessage(checkDbForExistingMessages(cbq.getMessage()));
            cbq.setFrom(checkDbForExistingUsers(cbq.getFrom()));
            return cbq;
        }
    }
//
//    CONVERT ENTITIES TO OBJ
//
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
        if (queryEntity.getInlineMessageId() != null)
        queryObj.setInline_message_id(queryEntity.getInlineMessageId());
        if (queryEntity.getData() != null)
            queryObj.setData(queryEntity.getData());
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
        userObj.setIs_bot(userEntity.getIsBot());
        userObj.setFirst_name(userEntity.getFirstName());
        if (userEntity.getLastName() != null)
        userObj.setLast_name(userEntity.getLastName());
        if (userEntity.getUsername() != null)
        userObj.setUsername(userEntity.getUsername());
        return userObj;
    }

//
//    CONVERT OBJ TO ENTITIES
//
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
        entityUser.setIsBot(user.getIs_bot());
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
        if (query.getMessage() != null)
        entityQuery.setMessage(convertMessageObjToEntity(query.getMessage()));
        if (query.getInline_message_id() != null)
        entityQuery.setInlineMessageId(query.getInline_message_id());
        if (query.getData() != null)
            entityQuery.setData(query.getData());
        return entityQuery;
    }
}
