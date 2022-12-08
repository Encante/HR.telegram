package net.ddns.encante.telegram.HR;

import net.ddns.encante.telegram.HR.RemoteRequest.UnirestRequest;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		@Autowired
		SendMessage()
				.setText("Bot odpalony T: " + Utils.getCurrentDateTime())
				.sendToMe();


	}
}
