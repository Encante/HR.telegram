package net.ddns.encante.telegram.HR.RemoteRequest;

import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessage;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.SentMessage;
import net.ddns.encante.telegram.HR.persistence.entities.HueAuthorizationEntity;
import net.ddns.encante.telegram.HR.persistence.entities.HueTokensEntity;

public interface RemoteRequest {
    SentMessage sendTelegramMessage(SendMessage message);
    SentMessage editTelegramMessage(EditMessage message);
    void answerCallbackQuery(AnswerCallbackQuery answer);
    HueAuthorizationEntity requestHueAuthentication(HueAuthorizationEntity authorization);
    HueTokensEntity refreshHueTokens(HueAuthorizationEntity authorization);
}
