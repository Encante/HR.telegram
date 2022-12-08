package net.ddns.encante.telegram.HR.RemoteRequest;

public interface RemoteRequest {
    Object sendMessageObject(Object message);
    Object editMessageObject(Object message);
    void printResponseToConsole();
    void putResponseDetailsInDb();
}
