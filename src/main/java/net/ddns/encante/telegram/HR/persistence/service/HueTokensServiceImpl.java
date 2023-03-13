package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.Hue.HueTokens;
import net.ddns.encante.telegram.HR.persistence.entities.HueTokensEntity;
import net.ddns.encante.telegram.HR.persistence.repository.HueTokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("hueTokensService")
public class HueTokensServiceImpl implements HueTokensService{
    @Autowired
    private HueTokensRepository repository;

    @Override
    public HueTokens saveHueTokens (HueTokens hueTokensObj){
        HueTokensEntity ent= convertObjToEntity(hueTokensObj);
        return convertEntitytoObj(repository.save(ent));
    }
    @Override
    public HueTokens getTokens(){
        if (repository.count()>1) return convertEntitytoObj(repository.findFirstByOrderByKeyIdAsc());
        else throw new RuntimeException("No tokens in DB");
    }
    @Override
    public HueTokens getBearerTokens(String bearer){
        if (repository.findByBearer(bearer)!= null)return convertEntitytoObj(repository.findByBearer(bearer));
        else return null;
    }

//
//    Convert Entities to Objects
//
    private HueTokens convertEntitytoObj (HueTokensEntity ent){
        HueTokens obj = new HueTokens (ent.getAccessToken(),ent.getExpiresIn(),ent.getRefreshToken(),ent.getTokenType());
        if (ent.getBearer()!= null)obj.setBearer(ent.getBearer());
        return obj;
    }
//
//    Convert Objects to Entities
//
    private HueTokensEntity convertObjToEntity (HueTokens obj){
        HueTokensEntity ent= new HueTokensEntity();
        ent.setAccessToken(obj.getAccessToken());
        ent.setExpiresIn(obj.getExpiresIn());
        ent.setRefreshToken(obj.getRefreshToken());
        if (obj.getBearer()!= null)ent.setBearer(obj.getBearer());
        return ent;
    }
}
