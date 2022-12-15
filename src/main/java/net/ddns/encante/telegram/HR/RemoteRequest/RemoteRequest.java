package net.ddns.encante.telegram.HR.RemoteRequest;

import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.SentMessage;

public interface RemoteRequest {
    SentMessage sendTelegramMessage(SendMessage message);
    SentMessage editTelegramMessage(EditMessage message);
//    void printResponseToConsole();
//    void putResponseDetailsInDb();
}
