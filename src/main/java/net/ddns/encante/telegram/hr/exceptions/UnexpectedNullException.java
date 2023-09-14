package net.ddns.encante.telegram.hr.exceptions;

import net.ddns.encante.telegram.hr.telegram.service.MessageManager;
import org.springframework.stereotype.Component;

@Component
public class UnexpectedNullException extends RuntimeException{
    private MessageManager mgr;
    public UnexpectedNullException(String errorCode, String errorMessage, MessageManager msgMgr){
        super(errorCode);
        this.mgr = msgMgr;

    }
}
