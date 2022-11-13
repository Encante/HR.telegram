package net.ddns.encante.telegram.HR.HR.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		RemoteRequest request = new RemoteRequest();
		request.sendMessageToChatId("Bot odpalony T: " + Utils.getCurrentDateTime(), 5580797031L);
	}

}
