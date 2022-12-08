package net.ddns.encante.telegram.HR.Messages;

import lombok.Getter;
import lombok.Setter;

// klasa na updaty wysyłane webhookami
//i nie tylko
@Getter @Setter
public class WebhookUpdate {

        Long update_id;
        Message message;
        CallbackQuery callback_query;
//        METODY
    void printUpdateToConsole(){
    if (message.getText()!=null){
        System.out.println("update id: " + getUpdate_id());
        System.out.println("chat id: " + message.getChat().getId());
        System.out.println("message id: " + message.getMessage_id());
        System.out.println("message: " + message.getText());
        System.out.println("From: " + message.getFrom().getFirst_name());
        if (message.getFrom().getLast_name() != null)
            System.out.println(message.getFrom().getLast_name());
        System.out.println("Is bot: " + message.getFrom().is_bot());
        }
    else {
        System.out.println("Not a text message!");
        System.out.println("update id: " + getUpdate_id());
        System.out.println("chat id: " + message.getChat().getId());
        System.out.println("message id: " + message.getMessage_id());
        System.out.println("From: " + message.getFrom().getFirst_name());
        if (message.getFrom().getLast_name() != null)
            System.out.println(message.getFrom().getLast_name());
        System.out.println("Is bot: " + message.getFrom().is_bot());
        }
    }
}

