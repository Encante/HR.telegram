package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.RemoteRequest.UnirestRequest;
import net.ddns.encante.telegram.HR.TelegramMethods.MessageManager;
import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import net.ddns.encante.telegram.HR.persistence.repository.HueAuthorizationRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service("hueAuthorizationService")
public class HueAuthorizationServiceImpl implements HueAuthorizationService{
    @Autowired
    private HueAuthorizationRepository repository;
    @Autowired
    MessageManager msgManager;
    @Autowired
    UnirestRequest request;
    private static final Logger log = LoggerFactory.getLogger("HueAuthorizationService");
// If given clientId is in base, update it with new data
    @Override
    public HueAuthorizationEntity saveOrUpdateAuthorizationBasedOnClientId(HueAuthorizationEntity ent){
        if (isClientIdAlreadyInDb(ent)) ent.setKeyId(checkDbForExistingClientId(ent).getKeyId());
        return repository.save(ent);
    }
    @Override
    public HueAuthorizationEntity getAuthorization(){
        if (repository.count()>0){
            return repository.findFirstByOrderByKeyIdAsc();
        }
        else throw new RuntimeException("No authorization in DB");
    }
    @Override
    public HueAuthorizationEntity getAuthorizationForDisplayName(String displayName){
        if (repository.findByDisplayName(displayName)!=null){
            return repository.findByDisplayName(displayName);
        }
        else {
            log.warn("No authorization for Display Name "+displayName);
            return null;
        }
    }
    @Override
    public HueAuthorizationEntity getAuthorizationForState(String state){
        if (repository.findByState(state)!=null){
            return repository.findByState(state);
        }
        else {
            log.warn("No authorization for state "+state);
            return null;
        }
    }
//    method for sending authorization link to chat
    @Override
    public void sendAuthorizationLink(@NotNull String displayName){
//        authorization for display name check
        if (getAuthorizationForDisplayName(displayName) != null) {
            HueAuthorizationEntity authorization = getAuthorizationForDisplayName(displayName);
//            check if authorization contains clientid
            if (authorization.getClientId()!= null){
                int leftLimit = 48; // numeral '0'
                int rightLimit = 122; // letter 'z'
                int targetStringLength = 10;
                Random random = new Random();
                authorization.setState(random.ints(leftLimit, rightLimit + 1)
                        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString());
                authorization.setCode(null);
                authorization.setTokens(null);
                authorization.setUsername(null);
//                now we send a valid link with authorization to user ->
                msgManager.sendBackTelegramTextMessage("https://api.meethue.com/v2/oauth2/authorize?client_id=" + authorization.getClientId() +
                        "&response_type=code&state=" + authorization.getState());
//                -> and persist authorization entity in db
                saveOrUpdateAuthorizationBasedOnClientId(authorization);
//                if authorization doesn't contain clientid we throw an exception
            }else {
                msgManager.sendAndLogErrorMsg("Authorization entity not valid. clientId is null. Invoker: HueAuthorizationServiceImpl.sendAuthorizationLink.");
            }
/*            public String generateAuthorizationLink(){
                if (this.clientId!= null) {
                    int leftLimit = 48; // numeral '0'
                    int rightLimit = 122; // letter 'z'
                    int targetStringLength = 10;
                    Random random = new Random();

                    this.state = random.ints(leftLimit, rightLimit + 1)
                            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                            .limit(targetStringLength)
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString();
                    this.code=null;
                    this.tokens = null;
                    this.username = null;
                    return "https://api.meethue.com/v2/oauth2/authorize?client_id=" + this.clientId+
                            "&response_type=code&state=" + this.state;
                }
                else {
                    log.warn("No clientId given or set");
                    throw new RuntimeException("No clientId given!");}
            }*/
        } else {
            msgManager.sendAndLogErrorMsg("No authorization for app with name '" + displayName + "' in DB. Invoker: HueAuthorizationServiceImpl.sendAuthorizationLink");
        }
    }
    @Override
    public void authenticateApp(@NotNull String state,@NotNull String code){
//        check if there is any authorization with given state code
        if (getAuthorizationForState(state)!= null){
            HueAuthorizationEntity authorization = getAuthorizationForState(state);
            authorization.setCode(code);
//            check crucial parts of authorization entity for possible null-pointers
            if(authorization.getClientId() != null
            && authorization.getClientSecret()!= null){
                saveOrUpdateAuthorizationBasedOnClientId(request.requestHueAuthentication(authorization));
                log.debug("JUST FOR DEBUGGING PURPOSES LETS CHECK IF ORIGINAL SENDER IS SET FOR MSGMANAGER(authenticateApp): "+msgManager.getOriginalSender().getId());
                msgManager.sendTelegramTextMessage("Tokens retrieved! App " + authorization.getDisplayName() + " authorized!", msgManager.getME());
//                if any of required authorization fields are missing
            }else msgManager.sendAndLogErrorMsg("ERROR.Part of Authorization missing. Invoker: HueAuthorizationServiceImpl.authenticateApp");
//            if there isn't any authorization with given state
        }else
            msgManager.sendAndLogErrorMsg("ERROR. No authorization with state: "+ state+" in DB. Invoker: HueAuthorizationServiceImpl.authenticateApp.");
    }

    private boolean checkAndRefreshHueAuthorization (@NotNull HueAuthorizationEntity authorization) {
        //                                            authorization token and username check
        if (authorization.getTokens().getAccess_token() != null && authorization.getUsername() != null) {
//                                                check if access token and username is valid
            if (request.hueGetResourceDevice(authorization) != null && request.hueGetResourceDevice(authorization).getErrors() == null) {
                log.debug("Tokens for " + authorization.getDisplayName() + " checked and valid.");
                return true;
            } else{
                return false;
            }
        }else {
            String err = "ERROR. Access token or username is null. Invoker: checkAndRefreshHueAuthorization";
            msgManager.sendAndLogErrorMsg(err);
            throw new RuntimeException(err);
        }
    }
    private HueAuthorizationEntity checkDbForExistingClientId(HueAuthorizationEntity ent){
        if (repository.findByClientId(ent.getClientId()) != null)
            return repository.findByClientId(ent.getClientId());
        else return ent;
    }
    private Boolean isClientIdAlreadyInDb (HueAuthorizationEntity ent){
        return repository.findByClientId(ent.getClientId()) != null;
    }
}
