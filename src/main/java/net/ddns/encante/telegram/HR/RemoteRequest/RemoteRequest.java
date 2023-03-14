package net.ddns.encante.telegram.HR.RemoteRequest;

import net.ddns.encante.telegram.HR.Hue.HueTokens;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.SentMessage;
import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;

public interface RemoteRequest {
    SentMessage sendTelegramMessage(SendMessage message);
    SentMessage editTelegramMessage(EditMessage message);
    void answerCallbackQuery(AnswerCallbackQuery answer);
//    HueTokens getHueTokens (HueAuthorizationEntity authorization);

}
