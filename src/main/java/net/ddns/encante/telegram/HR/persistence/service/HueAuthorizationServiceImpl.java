package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import net.ddns.encante.telegram.HR.persistence.repository.HueAuthorizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("hueAuthorizationService")
public class HueAuthorizationServiceImpl implements HueAuthorizationService{
    @Autowired
    private HueAuthorizationRepository repository;

    @Override
    public HueAuthorizationEntity saveAuthorization(HueAuthorizationEntity ent){
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
        else return null;
    }
}
