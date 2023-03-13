package net.ddns.encante.telegram.HR.Hue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HueAuthorization {
    String clientId;
    final String responseType = "code";
    String state;
    String deviceId;
    String deviceName;
    String appId;
    String code;

    HueAuthorization (String clientId, String appId){
        this.clientId = clientId;
        this.appId = appId;
    }

    public String generateAuthorizationLink (){
        return "https://api.meethue.com/v2/oauth2/authorize?client_id="+this.clientId+"&response_type="+this.responseType+"&state="+this.state;
    }

}
