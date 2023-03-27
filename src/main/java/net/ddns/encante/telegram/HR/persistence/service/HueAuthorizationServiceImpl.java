package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import net.ddns.encante.telegram.HR.persistence.repository.HueAuthorizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("hueAuthorizationService")
public class HueAuthorizationServiceImpl implements HueAuthorizationService{
    @Autowired
    private HueAuthorizationRepository repository;
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

    private HueAuthorizationEntity checkDbForExistingClientId(HueAuthorizationEntity ent){
        if (repository.findByClientId(ent.getClientId()) != null)
            return repository.findByClientId(ent.getClientId());
        else return ent;
    }
    private Boolean isClientIdAlreadyInDb (HueAuthorizationEntity ent){
        return repository.findByClientId(ent.getClientId()) != null;
    }
}
