package net.ddns.encante.telegram.hr.hue.service;

import lombok.extern.slf4j.Slf4j;
import net.ddns.encante.telegram.hr.Utils;
import net.ddns.encante.telegram.hr.hue.entity.HueAuthorization;
import net.ddns.encante.telegram.hr.hue.repository.HueAuthorizationRepository;
import net.ddns.encante.telegram.hr.request.UnirestRequest;
import net.ddns.encante.telegram.hr.telegram.service.MessageManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Random;
@Slf4j
@Service("hueAuthorizationService")
public class HueAuthorizationService{
    private HueAuthorizationRepository repository;
    MessageManager msgManager;
    UnirestRequest request;
    public HueAuthorizationService(HueAuthorizationRepository repository, MessageManager msgManager,UnirestRequest request){
        this.repository=repository;
        this.msgManager=msgManager;
        this.request=request;
    }
// If given clientId is in base, update it with new data
    public HueAuthorization saveOrUpdateAuthorizationBasedOnClientId(HueAuthorization ent){
        if (isClientIdAlreadyInDb(ent)) ent.setKeyId(checkDbForExistingClientId(ent).getKeyId());
        return repository.save(ent);
    }
    
    public HueAuthorization getFirstAuthorization(){
        if (repository.count()>0){
            return repository.findFirstByOrderByKeyIdAsc();
        }
        else {
            log.warn("No HUE App authorization in DB");
            throw new RuntimeException("No HUE App authorization in DB");
        }
    }
    
    public HueAuthorization getAuthorizationForDisplayName(String displayName){
        if (repository.findByDisplayName(displayName)!=null){
            return repository.findByDisplayName(displayName);
        }
        else {
            log.warn("No authorization for Display Name "+displayName);
            return null;
        }
    }
    
    public HueAuthorization getAuthorizationForState(String state){
        if (repository.findByState(state)!=null){
            return repository.findByState(state);
        }
        else {
            log.warn("No authorization for state "+state);
            return null;
        }
    }
//    method for sending authorization link to chat
    
    public void sendAuthorizationLink(@NotNull String displayName){
//        authorization for display name check
        if (getAuthorizationForDisplayName(displayName) != null) {
            HueAuthorization authorization = getAuthorizationForDisplayName(displayName);
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
        } else {
            msgManager.sendAndLogErrorMsg("No authorization for app with name '" + displayName + "' in DB. Invoker: HueAuthorizationServiceImpl.sendAuthorizationLink");
        }
    }
    
    public void authenticateApp(@NotNull String state,@NotNull String code){
//        check if there is any authorization with given state code
        if (getAuthorizationForState(state)!= null){
            HueAuthorization authorization = getAuthorizationForState(state);
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
    
    public String checkAndRefreshToken(@NotNull HueAuthorization authorization){
        if (!checkHueAuthorization(authorization)){
            return refreshHueToken(authorization);
//        if token is valid
        }else {
            log.debug("Token valid and no need to be refreshed.");
            return "Token valid and no need to be refreshed.";
        }
    }
//    PRIVATE
//
//

    private boolean checkHueAuthorization (@NotNull HueAuthorization authorization) {
        //                                            authorization token and username check
        if (authorization.getTokens().getAccess_token() != null && authorization.getUsername() != null) {
//                                                check if access token and username is valid
            if (request.hueGetResourceDevice(authorization) != null && request.hueGetResourceDevice(authorization).getErrors().size() == 0) {
                log.debug("Tokens for " + authorization.getDisplayName() + " checked and valid. checkHueAuthorization");
                return true;
            } else{
                log.debug("Token invalid. Must be refreshed");
                return false;
            }
        }else {
            String err = "ERROR. Access token or username is null. Invoker: checkAndRefreshHueAuthorization";
            msgManager.sendAndLogErrorMsg(err);
            throw new RuntimeException(err);
        }
    }
    private String refreshHueToken (@NotNull HueAuthorization authorization){
//        authorization check for null pointers
        if(authorization.getClientId() != null
        && authorization.getClientSecret()!= null
        && authorization.getTokens().getRefresh_token()!= null){
            authorization.setTokens(request.refreshHueTokens(authorization));
            authorization.getTokens().setCreated(Utils.getCurrentUnixTime());
            saveOrUpdateAuthorizationBasedOnClientId(authorization);
            log.debug("HUE Token refreshed and saved succesfully. refreshHueToken");
            return "HUE Token refreshed and saved succesfully.";
//            authorization has some unexpected nulls
        }else {
            String err = "ERROR. ClientId, ClientSecret or RefreshToken is null. Invoker: HueAuthorizationServiceImpl.refreshHueToken";
            msgManager.sendAndLogErrorMsg(err);
            return err;
        }
    }
    private HueAuthorization checkDbForExistingClientId(HueAuthorization ent){
        if (repository.findByClientId(ent.getClientId()) != null)
            return repository.findByClientId(ent.getClientId());
        else return ent;
    }
    private boolean isClientIdAlreadyInDb (HueAuthorization ent){
        return repository.findByClientId(ent.getClientId()) != null;
    }
}
