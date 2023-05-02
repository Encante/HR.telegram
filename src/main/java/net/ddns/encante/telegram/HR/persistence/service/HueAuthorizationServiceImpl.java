package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.TelegramMethods.MessageManager;
import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import net.ddns.encante.telegram.HR.persistence.repository.HueAuthorizationRepository;
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

    @Override
    public void sendAuthorizationLink(Long chatId, String displayName){
        if (getAuthorizationForDisplayName(displayName) != null) {
/**            public String generateAuthorizationLink(){
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
            }**/
            msgManager.sendTelegramTextMessage("")
        } else {
            msgManager.sendAndLogErrorMsg("No authorization for app with name '" + displayName + "' in DB. Invoker: HueAuthorizationServiceImpl.sendAuthorizationLink");
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
    private boolean checkAuthorizationForDisplayName(String displayName, String invoker){

            return false;
        }
    }
}
