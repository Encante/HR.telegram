package net.ddns.encante.telegram.HR.RemoteRequest;


public interface RemoteRequest {
    Object sendMessageObject(Object Message);
    Object editMessageObject(Object message);
    void printResponseToConsole(Object response);
    void putResponseDetailsInDb(Object response);
}
