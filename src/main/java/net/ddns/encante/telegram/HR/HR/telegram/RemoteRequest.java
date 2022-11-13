package net.ddns.encante.telegram.HR.HR.telegram;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

//@Component
public class RemoteRequest {
    private final String token = "***REMOVED***";

// chat id:
//    ja:   5580797031
//    Yana: 566760042

    public void sendMessageToChatId(String messageToSend, Long chat_id ){
        String url = "https://api.telegram.org/bot"+token+"/sendMessage?";

        HttpResponse<String> response = Unirest.post(url+
                "chat_id="+
                chat_id+
                "&text="+
                messageToSend)
                .asString();
        System.out.println(url+"\r\n");
        System.out.println("RESPONSE STATUS: \r\n" + response.getStatus() + " " + response.getStatusText()
                + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
                + "\r\nBODY: " + response.getBody());
    }

//    public void setWebhook (String address){
//
//    }

}
