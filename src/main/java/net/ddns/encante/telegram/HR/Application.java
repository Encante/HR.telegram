package net.ddns.encante.telegram.HR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class Application {

//	@Autowired
//	RemoteRequest request;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		SendMessage sendMessage = new SendMessage();
		sendMessage.setChat_id(5580797031L);
		sendMessage.setText("Bot odpalony T: "+ Utils.getCurrentDateTime());
		sendMessage.sendMessageToChatIdByObject(ReplyKeyboardType.NO);
		sendMessage.setText("So");
		sendMessage.sendMessageToChatIdByObject(ReplyKeyboardType.REPLY);
	}
}
