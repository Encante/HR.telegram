package net.ddns.encante.telegram.hr.exceptions;

import lombok.extern.slf4j.Slf4j;
import net.ddns.encante.telegram.hr.telegram.service.MessageManager;
@Slf4j
public class UnexpectedStateException extends RuntimeException{
    public UnexpectedStateException(String errorCode, String errorMessage, MessageManager mgr){
        super(errorCode);
        log.warn("Error code: "+errorCode+". "+errorMessage);
        mgr.sendBackTelegramTextMessage("Error code: "+errorCode);
    }
}
