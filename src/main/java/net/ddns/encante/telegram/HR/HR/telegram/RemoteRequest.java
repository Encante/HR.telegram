package net.ddns.encante.telegram.HR.HR.telegram;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

//@Component
public class RemoteRequest {
    private final String token = "***REMOVED***";

//    @Autowired
//    public RemoteRequest

    public void sendMessage (String messageToSend){
        String url = "https://api.telegram.org/bot"+token+"/sendMessage?";

        HttpResponse<String> response = Unirest.post(url+"chat_id=5580797031&text="+messageToSend).asString();
        System.out.println(url+"\r\n");
        System.out.println("RESPONCE STATUS: \r\n" + response.getStatus() + " " + response.getStatusText()
                + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
                + "\r\nBODY: " + response.getBody());
    }

}
