package net.ddns.encante.telegram.HR;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class RemoteRequest {
    private final String botToken = "***REMOVED***";
    private final String sendMessageUrl = "https://api.telegram.org/bot"+ botToken +"/sendMessage?";


public HttpResponse<JsonNode> sendMessageAsJson(String body){
    HttpResponse<JsonNode> response = Unirest.post(sendMessageUrl)
            .header("Content-Type", "application/json")
            .body(body)
            .asJson();
    System.out.println("BODY SENT: "+body);
    printResponseJsonToConsole(response);
    return response;
}


private void printResponseJsonToConsole(HttpResponse<JsonNode> response){
    System.out.println("RESPONSE STATUS: \r\n" + response.getStatus()
            + " "
            + response.getStatusText()
            + "\r\nHEADERS: \r\n" + response.getHeaders().toString()
            + "\r\nBODY: " + response.getBody().toPrettyString());
}

}
